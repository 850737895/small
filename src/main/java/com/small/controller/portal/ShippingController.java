package com.small.controller.portal;

import com.github.pagehelper.PageInfo;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.Shipping;
import com.small.pojo.User;
import com.small.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 收获地址controller
 * Created by Administrator on 2018/5/17.
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService shippingServiceImpl;

    /**
     * 添加收货地址
     * @param session session
     * @param shipping shipping
     * @return 返回shippingId
     */
    @RequestMapping("/add.do")
    @ResponseBody
    public SystemResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return shippingServiceImpl.add(shipping);
    }

    /**
     * 删除地址
     * @param session session
     * @param shippingId 收货地址Id
     * @return SystemResponse
     */
    @RequestMapping("/del.do")
    @ResponseBody
    public SystemResponse<String> del(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(shippingId == null) {
            return SystemResponse.createErrorByMsg("参数错误:shippingId 为null");
        }
        return shippingServiceImpl.del(user.getId(),shippingId);
    }

    @RequestMapping("/update.do")
    @ResponseBody
    public SystemResponse<String> update(HttpSession session,  Shipping shipping) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(shipping == null) {
            return SystemResponse.createErrorByMsg("参数错误:shipping 为null");
        }
        return shippingServiceImpl.update(user.getId(),shipping);
    }

    @RequestMapping("/select.do")
    @ResponseBody
    public SystemResponse<String> select(HttpSession session,  Integer shippingId) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        if(shippingId == null) {
            return SystemResponse.createErrorByMsg("参数错误:shippingId 为null");
        }
        return shippingServiceImpl.select(user.getId(),shippingId);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public SystemResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if(user == null) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return shippingServiceImpl.list(user.getId(),pageNum,pageSize);
    }
}
