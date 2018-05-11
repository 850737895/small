package com.small.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * 日期工具类
 * Created by 85073 on 2018/5/9.
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期字符串转为日期
     * @param dateStr 日期字符串
     * @param format 格式化
     * @return Date
     */
    public static Date DateStr2Date(String dateStr, String format) {
        if(StringUtils.isBlank(dateStr)) {
            return null;
        }
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateStr);
        return dateTime.toDate();
    }

    /**
     * 日期字符串
     * @param dateStr 日期字符串
     * @return Date
     */
    public static Date DateStr2Date(String dateStr) {
        if(StringUtils.isBlank(dateStr)) {
            return null;
        }
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(FORMAT);
        DateTime dateTime = dateTimeFormat.parseDateTime(dateStr);
        return dateTime.toDate();
    }

    /**
     * 日期转为日期字符串
     * @param date 日期
     * @param format 格式
     * @return String
     */
    public static String date2DateStr(Date date,String format) {
        if(date == null) {
            logger.error("传入的需要格式化字符串为null");
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime();
        return dateTime.toString(format);
    }


    /**
     * 日期转为日期字符串
     * @param date 日期
     * @return String
     */
    public static String date2DateStr(Date date) {
        if(date == null) {
            logger.error("传入的日期对象为null");
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime();
        return dateTime.toString(FORMAT);
    }
}
