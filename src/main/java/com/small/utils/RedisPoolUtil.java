package com.small.utils;

import com.small.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * redis连接池工具
 * Created by 85073 on 2018/5/29.
 */
@Slf4j
public class RedisPoolUtil {

    /**
     * set 操作
     * @param key 键
     * @param value 值
     * @return result
     */
    public static String set(String key,String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{},value:{},error:{}",key,value,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     *setex 操作
     * @param key key 键
     * @param expireTime 过期时间(m)
     * @param value 值
     * @return String
     */
    public static String setex(String key,int expireTime,String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,expireTime,value);
        } catch (Exception e) {
            log.error("setex key:{},expireTime:{},value:{},error:{}",key,expireTime,value,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * get 操作
     * @param key 键
     * @return value
     */
    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{}error:{}",key,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * 让key失效
     * @param key 键
     * @param expireTime 失效时间
     * @return Long
     */
    public static Long expire(String key,int expireTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key,expireTime);
        } catch (Exception e) {
            log.error("expire key:{},expireTime:{},error:{}",key,expireTime,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return boolean
     */
    public static boolean isExists(String key) {
        Jedis jedis = null;
        boolean result =false;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.exists(key);
        } catch (Exception e) {
            log.error("isExists key:{},error:{}",key,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }


    /**
     * 自增
     * @param key key
     * @return Long
     */
    public static Long incr(String key) {
        Jedis jedis = null;
        Long result =null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.incr(key);
        } catch (Exception e) {
            log.error("incr key:{},error:{}",key,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * 指定步长增加
     * @param key 键
     * @param step 步长
     * @return Long
     */
    public static Long incrBy(String key,Integer step) {
        Jedis jedis = null;
        Long result =null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.incrBy(key,step);
        } catch (Exception e) {
            log.error("incrBy key:{},step:{},error:{}",key,step,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * 递减
     * @param key key
     * @return Long
     */
    public static Long decr(String key) {
        Jedis jedis = null;
        Long result =null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.decr(key);
        } catch (Exception e) {
            log.error("decr key:{},error:{}",key,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * 指定步长递减
     * @param key 键
     * @param step 步长
     * @return Long
     */
    public static Long decrBy(String key,Integer step) {
        Jedis jedis = null;
        Long result =null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.incrBy(key,step);
        } catch (Exception e) {
            log.error("decrBy key:{},step:{},error:{}",key,step,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result =null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{},error:{}",key,e);
            RedisPool.returnBrokenResouce(jedis);
            return result;
        } finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }



    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        set("sex","男");
        setex("age",600,"18");
        System.out.println(get("sex"));
    }


}
