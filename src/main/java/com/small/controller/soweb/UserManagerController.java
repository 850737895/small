package com.small.controller.soweb;

import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * 后端用户管理工程
 * Created by 85073 on 2018/5/7.
 */
@Controller
@RequestMapping("/manager/user")
public class UserManagerController {

    @Autowired
    private IUserService userServiceImpl;

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    public SystemResponse<User> login(String username, String password, HttpSession session) {
        SystemResponse<User> userSystemResponse = userServiceImpl.doLogin(username,password);
        if(userSystemResponse.isSuccess()) {
            User user = userSystemResponse.getData();
            if(user.getRole() == SystemConst.Role.AMDIN) {
                session.setAttribute(SystemConst.CURRENT_USER,user);
                return userSystemResponse;
            }else{
                return SystemResponse.createErrorByMsg(SystemConst.NOT_ADMIN_AUTH);
            }
        }
        return userSystemResponse;
    }

}
