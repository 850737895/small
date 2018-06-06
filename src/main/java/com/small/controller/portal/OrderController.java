package com.small.controller.portal;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IOrderService;
import com.small.vo.OrderProductVo;
import com.small.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * 订单模块
 * Created by 85073 on 2018/5/20.
 */
@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {


    @Autowired
    private IOrderService orderServiceImpl;

    /**
     * 支付订单
     * @param request request对象
     * @param orderNo 订单id
     * @return 付款二维码地址
     */
    @RequestMapping("/pay.do")
    @ResponseBody
    public SystemResponse<Map<String,String>> pay(HttpServletRequest request,User user,Long orderNo) {
        if(orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的orderNo为null");
        }
        String path = request.getServletContext().getRealPath("upload");
        return orderServiceImpl.pay(user.getId(),orderNo,path);
    }

    /**
     * 支付宝回调
     * @param request request 对象
     * @return success
     */
    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public String aliPayCallBack(HttpServletRequest request) {
        Map<String,String[]> paramMap = request.getParameterMap();
        Map<String,String> resolverMap = Maps.newHashMap();

        //解析回调参数
        for(Iterator iterator=paramMap.keySet().iterator();iterator.hasNext();) {
            String key = (String) iterator.next();
            String [] values = paramMap.get(key);
            String valueStr = "";
            for (int index=0;index<values.length;index++) {
                valueStr = (index==values.length-1)?valueStr+values[index]:valueStr+","+values[index];
            }
            resolverMap.put(key,valueStr);
        }

        //处理支付宝回调逻辑
        try {
            return orderServiceImpl.aliPayCallBack(resolverMap);
        }catch (Exception e) {
            log.error("异常:{}",e.getMessage());
            return "failed";
        }
    }

    /**
     * 查询订单支付状态
     * @param orderNo  订单号
     * @return SystemResponse
     */
    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public SystemResponse queryOrderPayStatus(User user,Long orderNo) {
        if(orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的orderNo为null");
        }

        return orderServiceImpl.queryOrderPayStatus(user.getId(),orderNo);
    }

    /**
     * 生成订单
     * @param shippingId 收货地址ID
     * @return SystemResponse<OrderVo>
     */
    @RequestMapping("/createOrder.do")
    @ResponseBody
    public SystemResponse<OrderVo> createOrder(User user,Integer shippingId){

        if(shippingId == null) {
            return SystemResponse.createErrorByMsg("传入的收货地址为null");
        }
        try {
            return orderServiceImpl.createOrder(user.getId(),shippingId);
        }catch (Exception e) {
            log.error("创建订单失败:{}",e.getMessage());
            return SystemResponse.createErrorByMsg(e.getMessage());
        }
    }

    /**
     * 订单取消接口
     * @param orderNo orderNo对象
     * @return  SystemResponse<String>
     */
    @RequestMapping("/cancle.do")
    @ResponseBody
    public SystemResponse<String> cancle(User user,Long orderNo) {
        if(orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的订单号为null");
        }
        return orderServiceImpl.cancle(user.getId(),orderNo);
    }

    /**
     * 展示购物车勾选的物品
     * @return SystemResponse<OrderProductVo>
     */
    @RequestMapping("/get_order_cart_product.do")
    @ResponseBody
    public SystemResponse<OrderProductVo> getOrderCartProduct(User user) {

        return orderServiceImpl.getOrderCartProduct(user.getId());
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public SystemResponse<OrderVo> detail(User user ,Long orderNo) {
        if(orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的订单号为null");
        }
        return orderServiceImpl.detail(user.getId(),orderNo);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<PageInfo> list(User user, @RequestParam(value = "pageNum" ,defaultValue = "1")Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize)
    {
        return orderServiceImpl.list(user.getId(),pageNum,pageSize);
    }
}
