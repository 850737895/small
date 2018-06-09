package com.small.exceptions;

import com.small.common.SystemCode;
import lombok.Getter;

/**
 * Small业务异常
 * Created by 85073 on 2018/6/7.
 */
@Getter
public class SmallException extends RuntimeException{

    private Integer code;

    private String msg;

    public SmallException(Integer code,String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public SmallException(SystemCode systemCode) {
        super(systemCode.getMsg());
        this.code = systemCode.getCode();
        this.msg = systemCode.getMsg();
    }
}
