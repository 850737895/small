package com.small.common;

import com.google.common.collect.Lists;
import com.small.utils.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.List;

/**
 * 分布式redis
 * Created by Administrator on 2018/6/4.
 */
public class RedisShardingPool {

    private static ShardedJedisPool shardingRedisPool;

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

    /**IP*/
    private static String redis_server_ip2=PropertiesUtil.getPropertyValues("redis2.server.ip","47.104.128.12");
    /**port*/
    private static Integer redis_server_port2 = Integer.parseInt(PropertiesUtil.getPropertyValues("redis2.server.port","6380"));
    /**连接redis的password*/
    private static String redis_pass2 = PropertiesUtil.getPropertyValues("redis2.pass","123456");

    static {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdel);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMinIdle(minIdel);
        jedisPoolConfig.setTestOnReturn(testOnRetrun);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setBlockWhenExhausted(true);

        JedisShardInfo jedisShardInfo = new JedisShardInfo(redis_server_ip,redis_server_port);
        jedisShardInfo.setPassword(redis_pass);
        JedisShardInfo jedisShardInfo2 = new JedisShardInfo(redis_server_ip2,redis_server_port2);
        jedisShardInfo2.setPassword(redis_pass2);

        List<JedisShardInfo> shardInfos = Lists.newArrayList();
        shardInfos.add(jedisShardInfo);
        shardInfos.add(jedisShardInfo2);

        shardingRedisPool = new ShardedJedisPool(jedisPoolConfig,shardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    /**
     * 获取一个jedis实例
     * @return jedis实例
     */
    public static ShardedJedis getJedis() {
        return shardingRedisPool.getResource();
    }

    /**
     * 还回一个jedis实例
     * @param jedis jedis实例
     */
    public static void returnResource(ShardedJedis jedis) {
        shardingRedisPool.returnResource(jedis);
    }

    /**
     * 还回有问题的jedis 资源到 broken池中
     * @param jedis edis实例
     */
    public static void returnBrokenResouce(ShardedJedis jedis) {
        shardingRedisPool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = getJedis();
        for (int i = 0; i < 100; i++) {
            jedis.set("key"+i,"value"+i);
        }
    }
}
