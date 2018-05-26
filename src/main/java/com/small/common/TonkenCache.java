package com.small.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 本地guava缓存
 * Created by 85073 on 2018/5/6.
 */
@Slf4j
public class TonkenCache {


    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(100)
                                                            .maximumSize(10000).expireAfterWrite(12, TimeUnit.HOURS)
                                                            .build(new CacheLoader<String, String>() {
                                                                @Override
                                                                public String load(String s) throws Exception {
                                                                    return "null";
                                                                }
                                                            });
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            log.error("localCache get error",e);
        }
        return null;
    }
}
