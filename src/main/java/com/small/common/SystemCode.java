package com.small.common;

/**
 * 系统枚举
 * Created by 85073 on 2018/5/5.
 */
public enum SystemCode {

    SUCCESS(0,"SUCCESS"),

    ERROR(1,"ERROR"),

    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"),

    NEED_LOGIN(10,"NEED_LOGIN"),

    SERVER_ERROR(-1,"服务端异常")
    ;

    private Integer code;

    private String msg;

    SystemCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {

        return code;
    }

    public String getMsg() {
        return msg;
    }
}
