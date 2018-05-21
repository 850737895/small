package com.small.controller.portal;

import com.google.common.collect.Maps;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IOrderService;
import com.small.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * 订单模块
 * Created by 85073 on 2018/5/20.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderServiceImpl;

    @RequestMapping("/pay.do")
    @ResponseBody
    public SystemResponse<Map<String,String>> pay(HttpServletRequest request,Long orderNo) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);

        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
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
            logger.error("异常:{}",e.getMessage());
            return "failed";
        }
    }

    /**
     * 查询订单支付状态
     * @param request request对象
     * @param orderNo  订单号
     * @return SystemResponse
     */
    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public SystemResponse queryOrderPayStatus(HttpServletRequest request,Long orderNo) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);

        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的orderNo为null");
        }

        return orderServiceImpl.queryOrderPayStatus(user.getId(),orderNo);
    }

    /**
     * 生成订单
     * @param session session
     * @param shippingId 收货地址ID
     * @return SystemResponse<OrderVo>
     */
    public SystemResponse<OrderVo> createOrder(HttpSession session,Integer shippingId){

        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(shippingId == null) {
            return SystemResponse.createErrorByMsg("传入的收货地址为null");
        }

        try {
            return orderServiceImpl.createOrder(user.getId(),shippingId);
        }catch (Exception e) {
            logger.error("创建订单失败:{}",e.getMessage());
            return SystemResponse.createErrorByMsg(e.getMessage());
        }


    }
}
