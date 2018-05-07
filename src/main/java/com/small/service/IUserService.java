package com.small.service;

import com.small.common.SystemResponse;
import com.small.pojo.User;

/**
 * 前端用户交互service
 * Created by 85073 on 2018/5/5.
 */
public interface IUserService {

    /**
     * 用户登录
     * @param userName 用户名
     * @param password 密码
     * @return SystemResponse<User>
     */
     SystemResponse<User> doLogin(String userName,String password);

    /**
     * 用户注册接口
     * @param user 用户对象
     * @return 注册成功还是失败
     */
     SystemResponse<String> register(User user);

    /**
     * 表单校验(userName, email)
     * @param str 需要校验的值
     * @param type 需要校验的类型
     * @return SystemResponse
     */
     SystemResponse<String> checkValid(String str,String type);

    /**
     * 找回密码的问题
     * @param username  用户名
     * @return 问题
     */
    SystemResponse<String> selectQuestion(String username);

    /**
     * 校验用户找回 密码答案
     * @param username 用户名
     * @param question 问题
     * @param answer 密码
     * @return SystemResponse
     */
     SystemResponse<String> checkAnswer(String username,String question,String answer);

    /**
     * 忘记密码的重置密码
     * @param username 用户名
     * @param newPassword 新密码
     * @param forgetToken  token值
     * @return  SystemResponse
     */
     SystemResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken);

    /**
     * 重置密码(在线)
     * @param oldPassword 老密码
     * @param newPassword 新密码
     * @param user 用户对象
     * @return SystemResponse
     */
     SystemResponse<String> resetPassword(String oldPassword,String newPassword,User user);

    /**
     * 在线更新用户信息
     * @param user user对象
     * @return SystemResponse
     */
     SystemResponse<User> updateUserInfo(User user);

    /**
     * 获取用户信息
     * @param id  id
     * @return SystemResponse
     */
     SystemResponse<User> getInformation(Integer id);

}
