package com.small.service;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemResponse;
import com.small.pojo.Order;
import com.small.vo.OrderProductVo;
import com.small.vo.OrderVo;

import java.util.List;
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

    /**
     * 订单取消接口
     * @param userId 用户ID
     * @param orderNo  订单号
     * @return SystemResponse<String>
     */
    SystemResponse<String> cancle(Integer userId, Long orderNo);

    /**
     * 购物购物车勾选的产品
     * @param userId 用户ID
     * @return  SystemResponse<OrderProductVo>
     */
    SystemResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    /**
     * 查询订单详情
     * @param userId 用户I的
     * @param orderNo 订单号
     * @return  SystemResponse<OrderProductVo>
     */
    SystemResponse<OrderVo> detail(Integer userId, Long orderNo);

    /**
     * 查询订单列表
     * @param userId 用户id
     * @param pageNum 查询哪一页
     * @param pageSize  每一页大小
     * @return  SystemResponse<PageInfo>
     */
    SystemResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 后端管理订单详情
     * @param orderNo 订单ID
     * @return SystemResponse<OrderVo>
     */
    SystemResponse<OrderVo> managerOrderDetail(Long orderNo);

    /**
     * 管理后台查询订单列表
     * @param pageNum 分页下标
     * @param pageSize 每页的条数
     * @return  SystemResponse<PageInfo>
     */
    SystemResponse<PageInfo> managerList(Integer pageNum, Integer pageSize);

    /**
     * 订单查询
     * @param orderNo 订单号
     * @param pageNum 查询页码
     * @param pageSize 每页的条数
     * @return SystemResponse<PageInfo>
     */
    SystemResponse<PageInfo> orderSearch(Long orderNo, int pageNum, int pageSize);

    /**
     * 发货接口
     * @param orderNo 订单号
     * @return SystemResponse<String>
     */
    SystemResponse<String> manageSendGoods(Long orderNo);

    /** 定时关闭订单
     * @param noPayAfterCreateOrder 关单时间
     */
    void closeTimeoutOrder(Integer noPayAfterCreateOrder );
}
