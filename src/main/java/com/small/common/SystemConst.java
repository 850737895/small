package com.small.common;

/**
 * 系统常量类
 * Created by 85073 on 2018/5/5.
 */
public class SystemConst {

    public static final String USERNAME_NOT_EXIST = "用户名不存在";

    public static final String USER_NOT_LOGIN = "用户没有登录，无法获取用户信息";

    public static final String USERNAME_IS_REGISTER = "用户名已存在";

    public static final String EMAIL_IS_REGISTER = "邮箱已存在";

    public static final String PASSWORD_ERROR = "密码错误";

    public static final String REGISTER_FAIL = "用户注册失败";

    public static final String REGISTER_SUCCESS = "用户注册成功";

    public static final String CURRENT_USER = "current_user";

    public static final String CHECK_TYPE_USERNAME = "username";

    public static final String CHECK_TYPE_EMAIL = "email";

    public static final String UNSUPPORT_CHECK_TYPE = "不支持的参数校验类型";

    public static final String CHECK_PASS = "校验通过";

    public static final String ARGS_ERROR = "参数错误";

    public static final String SUBMIT_USERINFO_EMPTY = "提交的用户信息为空";

    public static final String QUESTION_IS_EMPTY = "找回密码的问题为空";

    public static final String ANSWER_IS_ERROR= "找回密码的答案错误";

    public static final String TOKEN_PRIFIX = "token_";

    public static final String LOCAL_TOKEN_REPAIR = "token 已失效";

    public static final String TOKEN_ERROR = "token错误,请重新获取重置密码的token";

    public static final String UPDATE_SUCCESS = "更新密码成功";

    public static final String UPDATE_ERROR= "更新密码失败";

    public static final String OLD_PASSWORD_ERROR = "老密码错误";

    public static final String UPDATE_USERINFO_SUCCESS = "更新用户信息成功";

    public static final String UPDATE_USERINFO_ERROR= "更新用户信息失败";

    public static final String NOT_ADMIN_AUTH = "不是管理员无法登录";


    public interface Role{

         Integer CUSTOMER = 1;

        Integer AMDIN = 0;
    }
}
