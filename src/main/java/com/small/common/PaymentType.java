package com.small.common;

/**
 * 支付类型(线上 线下 )
 * Created by 85073 on 2018/5/21.
 */
public enum PaymentType {

    ONLINE_PAY(1,"在线支付");

    PaymentType(int code,String value){
        this.code = code;
        this.value = value;
    }
    private String value;
    private int code;

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static  String codeOf(Integer code) {
        for(PaymentType paymentType:values()) {
            if(paymentType.getCode() == code) {
                return paymentType.getValue();
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }
}
