package com.small.common;

/**
 * 产品状态枚举
 * Created by 85073 on 2018/5/11.
 */
public enum ProductCode {

    ON_SALE(1,"产品在售"),
    DOWN(2,"产品下架"),
    DELETE(3,"删除")
    ;

    private Integer code;

    private String msg;

    ProductCode(Integer code, String msg) {
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
