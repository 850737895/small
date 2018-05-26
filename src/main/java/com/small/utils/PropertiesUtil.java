package com.small.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取properties工具类
 * Created by 85073 on 2018/5/7.
 */
@Slf4j
public class PropertiesUtil {


    public static  Properties properties =null;

    static {
        String filePath = "small.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath)));
        } catch (IOException e) {
            log.error("读取properties文件出错{}",e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getPropertyValues(String key,String defaultValue) {
        if(StringUtils.isBlank(key)) {
            log.warn("key的取值为空");
            return defaultValue;
        }
        String value = properties.getProperty(key.trim()).trim();
        return value;
    }

}
