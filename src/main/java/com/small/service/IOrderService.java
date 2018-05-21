package com.small.service;

import com.small.common.SystemResponse;
import com.small.vo.OrderVo;

import java.util.Map;

/**
 * 订单模块service
 * Created by 85073 on 2018/5/20.
 */
public interface IOrderService {

    /**
     * 订单支付接口
     * @param userId 用户ID
     * @param orderNo  用户订单号
     * @return
     * {
        "status": 0,
            "data": {
            "orderNo": "1485158676346",
            "qrPath": "http://img.happymmall.com/qr-1492329044075.png"
            }
        }
     */
     SystemResponse<Map<String,String>> pay(Integer userId,Long orderNo,String path);

    /**
     * 处理支付宝回调逻辑
     * @param inParam 支付宝回调参数
     * @return success|fail
     */
     String aliPayCallBack(Map<String,String> inParam) throws Exception;

    /**
     * 查询订单支付状态
     * @param userId 用户ID
     * @param orderNo  订单号
     * @return SystemResponse
     */
    SystemResponse queryOrderPayStatus(Integer userId, Long orderNo);

    /**
     * 生成订单
     * @param userId 用户ID
     * @param shippingId  收货地址I的
     * @return SystemResponse<OrderVo>
     */
    SystemResponse<OrderVo> createOrder(Integer userId, Integer shippingId) throws RuntimeException;
}
