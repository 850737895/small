package com.small.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.small.common.*;
import com.small.dao.*;
import com.small.pojo.*;
import com.small.service.IOrderService;
import com.small.utils.BigDecimalUtils;
import com.small.utils.DateUtil;
import com.small.utils.FTPUtil;
import com.small.utils.PropertiesUtil;
import com.small.vo.OrderItemVo;
import com.small.vo.OrderVo;
import com.small.vo.ShippingVo;
import com.sun.tracing.dtrace.Attributes;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.Random;

/**
 * 订单业务service
 * Created by 85073 on 2018/5/20.
 */
@Service
public class OrderServiceImpl implements IOrderService {

    public static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

    }

    @Override
    public SystemResponse<Map<String, String>> pay(Integer userId, Long orderNo,String path) {
        Map<String,String> retMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(null == order) {
            return SystemResponse.createErrorByMsg("用户没有该订单");
        }
        retMap.put("orderNo",orderNo.toString());

        AlipayTradePrecreateRequestBuilder builder = generatorAliPayResult(order);
        if(builder == null) {
            return SystemResponse.createErrorByMsg("用户订单中没有订单明细，无法发起支付");
        }
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        try {
            String qrUrl = dealAliPayStatus(result,path);
            retMap.put("qrPath",qrUrl);
        } catch (Exception e) {
            logger.error("异常信息:{}",e.getMessage());
            return SystemResponse.createErrorByMsg(e.getMessage());
        }
        return SystemResponse.createSuccessByData(retMap);
    }

    @Override
    public String aliPayCallBack(Map<String, String> inParam) throws Exception {
        if(inParam.containsKey("sign_type")) {
            inParam.remove("sign_type");
        }
        //small商城订单号(支付外部订单号)
        String outOrderNo = inParam.get("out_trade_no");
        //支付宝交易流水
        String tradeNo = inParam.get("trade_no");
        String tradeStatus = inParam.get("trade_status");

        Order order = orderMapper.selectByOrderNo(outOrderNo);
        if(null == order) {
            throw  new Exception("不是small商城的订单......");
        }
        //对参数进行验签(支付宝公钥验签)
        boolean sinatureFlag =  AlipaySignature.rsaCheckV2(inParam,Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
        if(!sinatureFlag) {
            throw new Exception("非法请求,验证不通过,再恶意请求我就报警找网警了");
        }
        if(order.getStatus()>= OrderStatusEnum.PAID.getCode()) {
            throw  new Exception("支付宝重复调用");
        }
        if(SystemConst.AliPayTradeStatus.TRADE_SUCCESS.equals(tradeStatus)) {
            //更新订单状态
            order.setUpdateTime(DateUtil.DateStr2Date(inParam.get("gmt_payment")));
            order.setStatus(OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        //校验订单金额
        BigDecimal localOrderMoney = order.getPayment();
        //支付宝回调金额
        BigDecimal aliPayOrderMoney = new BigDecimal(inParam.get("total_amount"));
        if(localOrderMoney.compareTo(aliPayOrderMoney)!=0) {
            throw  new Exception("商户系统中的金额和支付宝金额不一致");
        }

        //插入pay_info表
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(Long.valueOf(outOrderNo));
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(SystemConst.PayPlatForm.ALI_PAY);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return "success";
    }

    @Override
    public SystemResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(null == order) {
            return SystemResponse.createErrorByMsg("订单不存在........");
        }
        if(order.getStatus()>=OrderStatusEnum.PAID.getCode()) {
            return SystemResponse.createSuccess();
        }
        return SystemResponse.createError();
    }


    @Transactional
    @Override
    public SystemResponse<OrderVo> createOrder(Integer userId, Integer shippingId) throws RuntimeException {
        //根据用户ID 查询已经勾选的购物车
        List<Cart> cartList = cartMapper.selectCheckCartByUserId(userId);
        if(cartList==null || cartList.isEmpty()){
            throw new RuntimeException("购物车为空");
        }

        //通过购物车列表计算出订单总价
        SystemResponse systemResponse = this.generatorOrderItemListByCartList(userId,cartList);
        if(!systemResponse.isSuccess()) {
            return systemResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) systemResponse.getData();

        BigDecimal payment = calOrderTotalMoney(orderItemList);

        //插入购物车
        Order order = assembleOrder(userId,shippingId,payment);

        int rowCount = orderMapper.insert(order);
        if(rowCount<0) {
            throw new RuntimeException("创建订单失败");
        }
        //插入订单明细
        for(OrderItem orderItem:orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        try {
            //批量插入
            orderItemMapper.batchInsert(orderItemList);
            //减少库存
            reduceProductStock(orderItemList);
            //删除购物车
            clearCart(cartList);
        }catch (Exception e) {
            throw new RuntimeException("生成订单异常"+e.getMessage());
        }

        OrderVo orderVo =assembleOrderVo(order,orderItemList);

        //组装返回
        return SystemResponse.createSuccessByData(orderVo);
    }

    /**
     * 组装orderVO
     * @param order order对象
     * @param orderItemList orderItem list
     * @return OrderVo
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPayment(order.getPayment());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(orderVo.getStatus());
        orderVo.setPaymentTypeDesc(PaymentType.codeOf(order.getPaymentType()));
        orderVo.setStatusDesc(OrderStatusEnum.codeOf(order.getStatus()));
        orderVo.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.selectByIdAndUserId(order.getUserId(),order.getShippingId());
        if(shipping!=null) {
            orderVo.setShippingVo(assembleShippingVo(shipping));
            orderVo.setReceiverName(shipping.getReceiverName());
        }
        orderVo.setPaymentTime(DateUtil.date2DateStr(order.getPaymentTime()));
        orderVo.setSendTime(DateUtil.date2DateStr(order.getSendTime()));
        orderVo.setEndTime(DateUtil.date2DateStr(order.getEndTime()));
        orderVo.setCreateTime(DateUtil.date2DateStr(order.getCreateTime()));
        orderVo.setCloseTime(DateUtil.date2DateStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getPropertyValues("ftp.server.http.prefix",SystemConst.DEFAULT_IMG_SERVER));

        //填充orderItemVo
        List<OrderItemVo> orderItemVOList = Lists.newArrayList();
        for (OrderItem orderItem:orderItemList) {
            orderItemVOList.add(assembleOrderItemVo(orderItem));
        }
        orderVo.setOrderItemVoList(orderItemVOList);
        return orderVo;
    }

    /**
     * 把orderItem 转为 order
     * @param orderItem 订单明细对象
     * @return OrderItemVo
     */
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        BeanUtils.copyProperties(orderItem,orderItemVo);
        return orderItemVo;
    }

    /**
     * 填充收货地址
     * @param shipping  收货地址
     * @return ShippingVo
     */
    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        BeanUtils.copyProperties(shipping,shippingVo);
        return shippingVo;
    }

    private void clearCart(List<Cart> cartList) {
        for (Cart cart:cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for(OrderItem orderItem:orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        //邮费为0  暂时包邮
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        order.setPaymentType(PaymentType.ONLINE_PAY.getCode());
        //todo
        // 发货时间等等
        //付款时间等等
        return order;
    }

    /**
     * 计算订单总价
     * @param orderItemList
     * @return
     */
    private BigDecimal calOrderTotalMoney(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem: orderItemList) {
            payment= BigDecimalUtils.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**
     * 通过购物车列表生成订单明细
     * @param userId 用户Id
     * @param cartList 购物车列表
     * @return List<OrderItem>
     */
    private SystemResponse generatorOrderItemListByCartList(Integer userId,List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cart: cartList) {
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //校验产品是否下架
            if(product.getStatus() != ProductCode.ON_SALE.getCode()) {
                return SystemResponse.createErrorByMsg("产品:"+product.getName()+"以下架");
            }
            //校验库存
            if(cart.getQuantity()>product.getStock()) {
                return SystemResponse.createErrorByMsg("产品:"+product.getName()+"库存不足");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setUserId(userId);
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity()));

            orderItemList.add(orderItem);

        }
        return SystemResponse.createSuccessByData(orderItemList);
    }

    /**
     * 生成订单号(暂时用这种)
     * @return long
     */
    private long generateOrderNo() {
        return System.currentTimeMillis()+new Random().nextInt(100);
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    /**
     * 构建支付宝 支付对象
     * @param order 订单对象
     * @return AlipayF2FPrecreateResult
     */
    private AlipayTradePrecreateRequestBuilder generatorAliPayResult(Order order) {
        //构建订单号 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        String outOrderNo = order.getOrderNo().toString();

        //构建订单主题 如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuffer("").append("【Small商城】订单:").append(outOrderNo).append("消费").toString();

        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuffer("【订单】:").append(outOrderNo).append("购买商品共")
                                                   .append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        List<GoodsDetail> goodsDetails = generaGoodsDetail(order);
        if(goodsDetails == null) {
            return null;
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outOrderNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetails);

        return builder;
    }

    /**
     * 生成订单明细
     * @param order order对象
     * @return List<GoodsDetail>
     */
    private List<GoodsDetail> generaGoodsDetail(Order order) {
        List<GoodsDetail> goodsDetails = Lists.newArrayList();
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(),order.getUserId());
        if(orderItemList == null && orderItemList.size()==0) {
            return null;
        }
        for (OrderItem orderItem: orderItemList) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalUtils.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetails.add(goodsDetail);
        }
        return goodsDetails;
    }

    /**
     * @Param  result 阿里支付状态逻辑处理
     * 处理支付宝预支付状态逻辑
     */
    private String dealAliPayStatus( AlipayF2FPrecreateResult result,String path ) throws Exception {
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                //细节细节细节
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path,qrFileName);
                try {
                    FTPUtil.updateFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常",e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getPropertyValues("ftp.server.http.prefix","http://47.104.128.12/images/")+targetFile.getName();
                return qrUrl;
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                throw new Exception("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                throw new Exception("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                throw new Exception("不支持的交易状态，交易返回异常!!!");
        }
    }
}
