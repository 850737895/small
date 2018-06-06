package com.small.controller.portal;

import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IUserService;
import com.small.utils.CookieUtil;
import com.small.utils.JsonUtil;
import com.small.utils.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *  测试springsession 的单点登录
 * Created by 85073 on 2018/5/5.
 */
@Controller
@RequestMapping("/springSession/user")
public class SpringSessoinUserController {

    @Autowired
    private IUserService userServiceImpl;

    /**
     * 用户登录(从springsession 中读取)
     * @param userName 用户名
     * @param password 密码
     * @param httpSession 用户保存的session
     * @return  SystemResponse<User>
     */
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<User> login(@RequestParam("username")String userName,
                                      @RequestParam("password")String password,
                                      HttpSession httpSession,
                                      HttpServletResponse response)
    {
        SystemResponse<User> userSystemResponse = userServiceImpl.doLogin(userName,password);
        if(userSystemResponse.isSuccess()) {
            httpSession.setAttribute(SystemConst.CURRENT_USER,userSystemResponse.getData());
        }
        return userSystemResponse;
    }

    /**
     * 用户登出操作 （删除session）
     * @param request request
     * @return SystemResponse
     */
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(SystemConst.CURRENT_USER);
        return SystemResponse.createSuccess();
    }


    /**
     * 获取用户的信息
     * @return SystemResponse
     */
    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<User> getUserInfo(HttpSession session) {

        return SystemResponse.createSuccessByData(session.getAttribute(SystemConst.CURRENT_USER));
    }

}