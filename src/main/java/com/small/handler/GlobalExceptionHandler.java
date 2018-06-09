package com.small.handler;

import com.small.common.SystemCode;
import com.small.common.SystemResponse;
import com.small.exceptions.SmallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常
 * Created by 85073 on 2018/6/7.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public SystemResponse handlerException(HttpServletRequest request, Exception e) {

        String reqUri = request.getRequestURI();
        log.error("请求:{}异常,异常原因:{}",reqUri,e);
        if(e instanceof SmallException) {
            SmallException smallException = (SmallException) e;
            return SystemResponse.createErrorByCodeMsg(smallException.getCode(),smallException.getMsg());
        }else {
            return SystemResponse.createErrorByCodeMsg(SystemCode.SERVER_ERROR.getCode(),e.getMessage());
        }
    }

    public GlobalExceptionHandler() {
        System.out.println("GlobalExceptionHandler......初始化.............");
    }
}
