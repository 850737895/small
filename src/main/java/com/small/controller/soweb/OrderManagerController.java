package com.small.controller.soweb;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IOrderService;
import com.small.service.IUserService;
import com.small.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 订单管理后台控制器
 * Created by 85073 on 2018/5/23.
 */
@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {

    @Autowired
    private IOrderService orderServiceImpl;

    @Autowired
    private IUserService userServiceImpl;

    /**
     * 后端订单详情
     *
     * @param session session
     * @param orderNo 订单号
     * @return SystemResponse<OrderVo>
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public SystemResponse<OrderVo> detail(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if (null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        if (!userServiceImpl.checkAmdinRole(user)) {
            return SystemResponse.createErrorByMsg(SystemConst.NOT_ADMIN_AUTH);
        }
        if (orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的参数:orderNo为空");
        }

        return orderServiceImpl.managerOrderDetail(orderNo);
    }

    /**
     * 后端列表查询
     *
     * @param session  session
     * @param pageNum  pageNum
     * @param pageSize pageSize
     * @return SystemResponse<PageInfo>
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if (null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        if (!userServiceImpl.checkAmdinRole(user)) {
            return SystemResponse.createErrorByMsg(SystemConst.NOT_ADMIN_AUTH);
        }
        return orderServiceImpl.managerList(pageNum, pageSize);
    }

    /**
     * 后端订单查询
     *
     * @param session  session
     * @param orderNo  订单号
     * @param pageNum  pageNum
     * @param pageSize pageSize
     * @return SystemResponse<PageInfo>
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public SystemResponse<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if (null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        if (!userServiceImpl.checkAmdinRole(user)) {
            return SystemResponse.createErrorByMsg(SystemConst.NOT_ADMIN_AUTH);
        }
        return orderServiceImpl.orderSearch(orderNo, pageNum, pageSize);
    }

    /**
     * 订单发货管理
     * @param session session
     * @param orderNo 订单号
     * @return SystemResponse<String>
     */
    @RequestMapping("/send_goods.do")
    @ResponseBody
    public SystemResponse<String> orderSendGoods(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if (null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        if (!userServiceImpl.checkAmdinRole(user)) {
            return SystemResponse.createErrorByMsg(SystemConst.NOT_ADMIN_AUTH);
        }
        if (orderNo == null) {
            return SystemResponse.createErrorByMsg("传入的参数:orderNo为空");
        }
        return orderServiceImpl.manageSendGoods(orderNo);
    }
}