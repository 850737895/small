package com.small.common;

import com.small.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis连接池
 * Created by 85073 on 2018/5/29.
 */
@Slf4j
public class RedisPool {
    /**jedis连接词*/
    private static JedisPool jedisPool;
    /**jedispool中jedis最大的可用实例*/
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getPropertyValues("redis.maxTotal","50"));
    /**jedispool中jedis 最大空闲数*/
    private static Integer maxIdel = Integer.parseInt(PropertiesUtil.getPropertyValues("redis.maxIdel","20"));
    /**jedispool中最小空闲数*/
    private static Integer minIdel = Integer.parseInt(PropertiesUtil.getPropertyValues("redis.minIdel","5"));
    /**从连接池中借出的jedis都会经过测试*/
    private static boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getPropertyValues("redis.testOnBorrow","true"));
    /**返回jedis到池中Jedis 实例都会经过测试*/
    private static boolean testOnRetrun = Boolean.parseBoolean(PropertiesUtil.getPropertyValues("redis.testOnRetrun","false"));
    /**IP*/
    private static String redis_server_ip=PropertiesUtil.getPropertyValues("redis.server.ip","47.104.128.12");
    /**port*/
    private static Integer redis_server_port = Integer.parseInt(PropertiesUtil.getPropertyValues("redis.server.port","6379"));
    /**连接redis的password*/
    private static String redis_pass = PropertiesUtil.getPropertyValues("redis.pass","123456");

    /**
     * 初始化redis连接池
     */
    private static void initPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdel);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMinIdle(minIdel);
        jedisPoolConfig.setTestOnReturn(testOnRetrun);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);

        jedisPool = new JedisPool(jedisPoolConfig,redis_server_ip,redis_server_port,1000*1,"123456");
    }

    static {
        initPool();
    }

    /**
     * 获取一个jedis实例
     * @return jedis实例
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 还回一个jedis实例
     * @param jedis jedis实例
     */
    public static void returnResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    /**
     * 还回有问题的jedis 资源到 broken池中
     * @param jedis edis实例
     */
    public static void returnBrokenResouce(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = getJedis();
        jedis.set("name","zhuwei");
        returnResource(jedis);
        jedisPool.destroy();
    }


}
