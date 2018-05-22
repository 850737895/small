package com.small.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取properties工具类
 * Created by 85073 on 2018/5/7.
 */
public class PropertiesUtil {

    public static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    public static  Properties properties =null;

    static {
        String filePath = "small.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath)));
        } catch (IOException e) {
            logger.error("读取properties文件出错{}",e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getPropertyValues(String key,String defaultValue) {
        if(StringUtils.isBlank(key)) {
            logger.warn("key的取值为空");
            return defaultValue;
        }
        String value = properties.getProperty(key.trim()).trim();
        return value;
    }

}
