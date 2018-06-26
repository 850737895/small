package com.small.utils;


/**
 * 分布式锁工具类
 * Created by Administrator on 2018/6/14.
 */
public class RedisLock {

    /**
     * 尝试获取锁
     * @param lock  锁
     * @param lockValue 锁的客户端标识
     * @param nx key不存在才设置
     * @param expx  px  失效单位 毫秒
     * @param expireTime  失效时长
     * @return  是否获取到锁
     */
    public static boolean tryLock(String lock,String lockValue,String nx,String expx,int expireTime) {
        boolean getLock =  false;
        String setLockFlag = RedisPoolUtil.set(lock,lockValue,nx,expx,expireTime*1000);
        if(setLockFlag!=null && setLockFlag.equals("OK")){
            getLock = true;
        }
        return getLock;
    }

    /**
     * 释放锁资源
     * @param lock 锁
     * @param lockValue 锁身份标识
     */
    public static void unlock(String lock,String lockValue) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisPoolUtil.eval(script,lock,lockValue);
    }
}
