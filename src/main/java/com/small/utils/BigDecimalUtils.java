package com.small.utils;

import java.math.BigDecimal;

/**
 * BigDecimal计算类
 * Created by 85073 on 2018/5/16.
 */
public class BigDecimalUtils {

    /**
     * 加法
     * @param v1 变量1
     * @param v2 变量2
     * @return 结果
     */
    public static BigDecimal add(double v1,double v2) {
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.add(b2);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 减法
     * @param v1 变量1
     * @param v2 变量2
     * @return 结果
     */
    public static BigDecimal sub(double v1,double v2) {
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.subtract(b2);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 乘法
     * @param v1 变量1
     * @param v2 变量2
     * @return 结果
     */
    public static BigDecimal mul(double v1,double v2) {
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.multiply(b2);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 除法
     * @param v1 变量1
     * @param v2 变量2
     * @return 结果
     */
    public static BigDecimal div(double v1,double v2) {
        try {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
