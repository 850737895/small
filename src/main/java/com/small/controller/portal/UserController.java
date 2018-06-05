package com.small.controller.portal;

import com.small.utils.CookieUtil;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IUserService;
import com.small.utils.JsonUtil;
import com.small.utils.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
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
 *  前端用户交互控制层
 * Created by 85073 on 2018/5/5.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userServiceImpl;

    /**
     * 用户登录
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
        String token = httpSession.getId();
        SystemResponse<User> userSystemResponse = userServiceImpl.doLogin(userName,password);
        if(userSystemResponse.isSuccess()) {
            String userStr = JsonUtil.obj2Str(userSystemResponse.getData());
            RedisPoolUtil.setex(httpSession.getId(),SystemConst.SessionCacheTime.SESSION_IN_REDIS_EXPIRE,userStr);
            CookieUtil.writeCookie(response,token);
        }
        return userSystemResponse;
    }

    /**
     * 用户登出操作
     * @param request request
     * @param response response
     * @return SystemResponse
     */
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> logout(HttpServletRequest request,HttpServletResponse response) {
        CookieUtil.delCookie(request,response);
        return SystemResponse.createSuccess();
    }

    /**
     * 用户注册
     * @param user 用户信息
     * @return  SystemResponse
     */
    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> register(User user) {
        return userServiceImpl.register(user);
    }

    /**
     * 用户校验
     * @param str 校验的值
     * @param type 校验的类型
     * @return SystemResponse
     */
    @RequestMapping(value = "/check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> checkValid(String str,String type) {
        return userServiceImpl.checkValid(str,type);
    }

    /**
     * 获取用户的信息
     * @param request request
     * @return SystemResponse
     */
    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<User> getUserInfo(HttpServletRequest request) {

        String cookieValue = CookieUtil.readCookie(request);

        if(StringUtils.isBlank(cookieValue)) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        String userStr = RedisPoolUtil.get(cookieValue);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByMsg("用户session以失效,请重新登录");
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if(null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        return SystemResponse.createSuccessByData(user);
    }

    /**
     * 忘记密码:获取找回密码中的 提示问题
     * @return 问题
     */
    @RequestMapping(value = "/forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> forgetGetQuestion(String username) {
        return userServiceImpl.selectQuestion(username);
    }

    /**
     * 校验问题答案
     * @param username 用户名
     * @param question  问题
     * @param answer  密码
     * @return SystemResponse
     */
    @RequestMapping(value = "/forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> forgetCheckAnswer(String username,String question,String answer) {
        return userServiceImpl.checkAnswer(username,question,answer);
    }

    /**
     * 忘记密码:获取找回密码中的 提示问题
     * @return 问题
     */
    @RequestMapping(value = "/forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken) {
        return userServiceImpl.forgetResetPassword(username,newPassword,forgetToken);
    }

    /**
     * 在线更新密码
     * @param oldPassword 老密码
     * @param newPassword  新密码
     * @param request request
     * @return  SystemResponse
     */
    @RequestMapping(value = "/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> resetPassword(String oldPassword,String newPassword,HttpServletRequest request) {
        String token = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(token)) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        String userStr = RedisPoolUtil.get(token);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByMsg("用户session以失效,请重新登录");
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if ( null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        return userServiceImpl.resetPassword(oldPassword,newPassword,user);
    }

    /**
     * 更新用户信息
     * @param user user对象
     * @param request request
     * @return SystemResponse
     */
    @RequestMapping(value = "/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> updateUserInfo(User user,HttpServletRequest request) {
        String token = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(token)) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        String userStr = RedisPoolUtil.get(token);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByMsg("用户session以失效,请重新登录");
        }
        User sessionUser= JsonUtil.str2Obj(userStr,User.class);
        if ( null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        user.setId(sessionUser.getId());
        user.setUsername(sessionUser.getUsername());
        SystemResponse<User> userSystemResponse = userServiceImpl.updateUserInfo(user);
        if(userSystemResponse.isSuccess()) {
            RedisPoolUtil.setex(token,SystemConst.SessionCacheTime.SESSION_IN_REDIS_EXPIRE,JsonUtil.obj2Str(user));
            return SystemResponse.createSuccessByMsg(SystemConst.UPDATE_USERINFO_SUCCESS);
        }else {
            return SystemResponse.createErrorByMsg(SystemConst.UPDATE_USERINFO_ERROR);
        }
    }

    /**
     * 需要强制登录
     * @param request request
     * @return
     */
    @RequestMapping(value = "/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<User> getInformation(HttpServletRequest request) {
        String tooken = CookieUtil.readCookie(request);
        if(StringUtils.isBlank(tooken)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        String userStr = RedisPoolUtil.get(tooken);
        if(StringUtils.isBlank(userStr)) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        User user = JsonUtil.str2Obj(userStr,User.class);
        if (null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return userServiceImpl.getInformation(user.getId());
    }


}