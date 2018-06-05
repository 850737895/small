package com.small.controller.soweb;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemResponse;
import com.small.service.IOrderService;
import com.small.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 订单管理后台控制器
 * Created by 85073 on 2018/5/23.
 */
@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {

    @Autowired
    private IOrderService orderServiceImpl;


    /**
     * 后端订单详情
     *
     * @param orderNo 订单号
     * @return SystemResponse<OrderVo>
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public SystemResponse<OrderVo> detail(Long orderNo) {
        if (orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的参数:orderNo为空");
        }
        return orderServiceImpl.managerOrderDetail(orderNo);
    }

    /**
     * 后端列表查询
     *
     * @param pageNum  pageNum
     * @param pageSize pageSize
     * @return SystemResponse<PageInfo>
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return orderServiceImpl.managerList(pageNum, pageSize);
    }

    /**
     * 后端订单查询
     *
     * @param orderNo  订单号
     * @param pageNum  pageNum
     * @param pageSize pageSize
     * @return SystemResponse<PageInfo>
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public SystemResponse<PageInfo> orderSearch(Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return orderServiceImpl.orderSearch(orderNo, pageNum, pageSize);
    }

    /**
     * 订单发货管理
     * @param orderNo 订单号
     * @return SystemResponse<String>
     */
    @RequestMapping("/send_goods.do")
    @ResponseBody
    public SystemResponse<String> orderSendGoods( Long orderNo) {
        if (orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的参数:orderNo为空");
        }
        return orderServiceImpl.manageSendGoods(orderNo);
    }
}