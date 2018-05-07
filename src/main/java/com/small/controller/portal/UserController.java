package com.small.controller.portal;

import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.pojo.User;
import com.small.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
                                      HttpSession httpSession) {
        SystemResponse<User> userSystemResponse = userServiceImpl.doLogin(userName,password);
        if(userSystemResponse.isSuccess()) {
            httpSession.setAttribute(SystemConst.CURRENT_USER,userSystemResponse.getData());
        }
        return userSystemResponse;
    }

    /**
     * 用户登出操作
     * @param httpSession httpSession
     * @return SystemResponse
     */
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute(SystemConst.CURRENT_USER);
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
     * @param session session
     * @return SystemResponse
     */
    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
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
     * @param session session
     * @return  SystemResponse
     */
    @RequestMapping(value = "/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> resetPassword(String oldPassword,String newPassword,HttpSession session) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if ( null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        return userServiceImpl.resetPassword(oldPassword,newPassword,user);
    }

    /**
     * 更新用户信息
     * @param user user对象
     * @param session session
     * @return SystemResponse
     */
    @RequestMapping(value = "/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<String> updateUserInfo(User user,HttpSession session) {
        User currentUser = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if ( null == currentUser) {
            return SystemResponse.createErrorByMsg(SystemConst.USER_NOT_LOGIN);
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        SystemResponse<User> userSystemResponse = userServiceImpl.updateUserInfo(user);
        if(userSystemResponse.isSuccess()) {
            session.setAttribute(SystemConst.CURRENT_USER,user);
            return SystemResponse.createSuccessByMsg(SystemConst.UPDATE_USERINFO_SUCCESS);
        }else {
            return SystemResponse.createErrorByMsg(SystemConst.UPDATE_USERINFO_ERROR);
        }
    }

    /**
     * 需要强制登录
     * @param session
     * @return
     */
    @RequestMapping(value = "/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse<User> getInformation(HttpSession session) {
        User user = (User) session.getAttribute(SystemConst.CURRENT_USER);
        if (null == user) {
            return SystemResponse.createErrorByCodeMsg(SystemCode.NEED_LOGIN.getCode(),SystemCode.NEED_LOGIN.getMsg());
        }
        return userServiceImpl.getInformation(user.getId());
    }


}
