package com.small.common;

import com.small.utils.JsonUtil;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 系统响应对象
 * Created by 85073 on 2018/5/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SystemResponse<T> implements Serializable{

    private Integer status;

    private String msg;

    private T  data;

    private SystemResponse(Integer status) {
        this.status = status;
    }

    private SystemResponse(Integer status,String msg) {
        this.status = status;
        this.msg = msg;
    }

    private SystemResponse(Integer status,T data) {
        this.status = status;
        this.data = data;
    }

    private SystemResponse(Integer status,String msg,T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 在json中不返回给前端
     * @return
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == SystemCode.SUCCESS.getCode();
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /********************创建返回失败响应对象*******************************/

    public static  <T> SystemResponse<T> createSuccess() {
        return new SystemResponse<T>(SystemCode.SUCCESS.getCode());
    }

    public static <T> SystemResponse createSuccessByMsg(String msg) {
        return  new SystemResponse<T>(SystemCode.SUCCESS.getCode(),msg);
    }

    public static <T> SystemResponse createSuccessByData(T data) {
        return  new SystemResponse<T>(SystemCode.SUCCESS.getCode(),data);
    }

    public static <T> SystemResponse createSuccessByMsgData(String msg,T data) {
        return  new SystemResponse<T>(SystemCode.SUCCESS.getCode(),SystemCode.SUCCESS.getMsg(),data);
    }

    /********************创建返回失败响应对象*******************************/

    public static  <T> SystemResponse<T> createError() {
        return new SystemResponse<T>(SystemCode.ERROR.getCode());
    }

    public static <T> SystemResponse<T> createErrorByMsg(String msg) {
        return new SystemResponse<T>(SystemCode.ERROR.getCode(),msg);
    }

    public static <T> SystemResponse<T> createErrorByCodeMsg(Integer errorCode,String msg) {
        return new SystemResponse<T>(errorCode,msg);
    }

    public static void main(String[] args) {
        System.out.println(JsonUtil.obj2StrPretty(SystemResponse.createErrorByCodeMsg(10,"111")));
    }

}
