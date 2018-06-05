package com.small.controller.soweb;

import com.small.utils.CookieUtil;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IUserService;
import com.small.utils.JsonUtil;
import com.small.utils.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
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
    @ResponseBody
    public SystemResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response) {
        SystemResponse<User> userSystemResponse = userServiceImpl.doLogin(username,password);
        if(userSystemResponse.isSuccess()) {
            User user = userSystemResponse.getData();
            if(user.getRole() == SystemConst.Role.AMDIN) {
                String token = session.getId();
                RedisPoolUtil.setex(token,SystemConst.SessionCacheTime.SESSION_IN_REDIS_EXPIRE, JsonUtil.obj2Str(user));
                CookieUtil.writeCookie(response,token);
                return userSystemResponse;
            }else{
                return SystemResponse.createErrorByMsg(SystemConst.NOT_ADMIN_AUTH);
            }
        }
        return userSystemResponse;
    }

}
