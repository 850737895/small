package com.small.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 本地guava缓存
 * Created by 85073 on 2018/5/6.
 */
public class TonkenCache {

    public static final Logger logger = LoggerFactory.getLogger(TonkenCache.class);

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
            logger.error("localCache get error",e);
        }
        return null;
    }
}
