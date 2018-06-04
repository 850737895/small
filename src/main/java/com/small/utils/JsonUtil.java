package com.small.utils;

import avro.shaded.com.google.common.collect.Lists;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.small.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Json工具类
 * Created by Administrator on 2018/5/30.
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //序列化的时候序列对象的所有属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //出现空bean得时候 序列化不报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        //关闭时间转为时间戳
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        //设置序列化时间显示格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.FORMAT));
        //反序列化时候，bean中的属性没有在json字符串中
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }

    private JsonUtil (){};

    /**
     * 序列化
     * @param data 对象
     * @param <T> 指定是泛型方法
     * @return String
     */
    public static <T>  String obj2Str(T data) {
        if(data == null) {
            return null;
        }
        Class clazz = data.getClass();
        if(clazz == String.class) {
            return (String)data;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("序列化对象{}失败，原因:{}",data,e);
            return null;
        }
    }

    /**
     * 反序列化
     * @param objStr 对象字符串
     * @param clazz 需要发序列话的对象
     * @param <T> 泛型方法
     * @return 对象
     */
    public static <T> T str2Obj(String objStr,Class<T> clazz) {
        if(StringUtils.isEmpty(objStr) || clazz == null) {
            return null;
        }
        if(clazz == String.class) {
            return (T)objStr;
        }
        try {
            return objectMapper.readValue(objStr,clazz);
        } catch (IOException e) {
            log.warn("反序列化对象{objStr}失败,原因:{}",objStr,e);
            return null;
        }
    }

    /**
     * 反序列化失败
     * @param objStr 对象字符串
     * @param typeReference 需要反序列化的类型
     * @param <T> 标识泛型方法
     * @return
     */
    public static <T> T str2Obj(String objStr, TypeReference<T> typeReference) {
        if(StringUtils.isEmpty(objStr) || typeReference == null) {
            return null;
        }
        if(typeReference.getType() == String.class) {
            return (T)objStr;
        }
        try {
            return objectMapper.readValue(objStr,typeReference);
        } catch (IOException e) {
            log.warn("反序列化对象{objStr}失败,原因:{}",objStr,e);
            return null;
        }
    }

<<<<<<< HEAD
    public static <T> T string2Obj(String str,Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
=======
    public static <T> T str2Obj(String objStr, Class<?> collectionsClass,Class<?>...elementsClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionsClass,elementsClass);
        try {
            return objectMapper.readValue(objStr,javaType);
        } catch (Exception e) {
            log.warn("反序列化对象:{},collectionsClass:{},elementsClass:{},原因:{} ",objStr,collectionsClass,elementsClass,e);
>>>>>>> cd5de35ad5b89501bbe185d86d47434607c4c23c
            return null;
        }
    }

    /**
     * 序列化格式化json
     * @param data 带序列化方法
     * @param <T> 指定泛型方法
     * @return String
     */
    public static <T> String obj2StrPretty(T data) {
        if(data == null) {
            return null;
        }
        Class clazz = data.getClass();
        if(clazz == String.class) {
            return (String) data;
        }
        try {
            return  objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("序列化对象{}失败，原因:{}",data,e);
            return null;
        }
    }


    public static void main(String[] args) {
        User user = new User();

        user.setId(12);
        user.setUpdateTime(new Date());
        user.setPassword("11111");
        String userStr = obj2Str(user);
        System.out.println(userStr);

        User user2 = str2Obj(userStr,User.class);
        System.out.println(user2.toString());



        List<User> list = Lists.newArrayList(user,user2);

        String listStr = obj2StrPretty(list);
        List<User> list2 = str2Obj(listStr, new TypeReference<List<User>>() {});
        List<User> list3 = str2Obj(listStr,List.class,User.class);
        System.out.println(list3);
        System.out.println(list2);
        System.out.println(str2Obj("2.5", new TypeReference<Float>() {}));
        Float  text= str2Obj("2.5", new TypeReference<Float>() {});

    }
}
