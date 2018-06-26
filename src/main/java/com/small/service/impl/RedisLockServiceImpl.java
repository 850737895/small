package com.small.service.impl;

import com.small.common.RedisPool;
import com.small.common.SystemConst;
import com.small.service.IOrderService;
import com.small.service.IRedisLockService;
import com.small.utils.RedisLock;
import com.small.utils.RedisPoolUtil;
import com.small.utils.RedisShardingPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/6/13.
 */
@Service
@Slf4j
public class RedisLockServiceImpl implements IRedisLockService {

    private static final Integer expireTime = 60*1000;

    @Autowired
    private IOrderService orderServiceImpl;

    @Override
    public void lockV1ForCloseOrder() {
        String lockValue = System.currentTimeMillis()+expireTime+"";
        long isGetLock = RedisShardingPoolUtil.setnx("lockName",lockValue);
        if(isGetLock == 1) {//获取锁成功

            //为了防止死锁,调用锁的失效时间,  但是使用单层防死锁 ,进程A 获取了锁 但是没执行expire语句 ，会导致死锁
            RedisShardingPoolUtil.expire("lockName",expireTime);
            orderServiceImpl.closeTimeoutOrder(2);
        }else{
            log.info("获取锁失败");
        }
    }


    /**
     * 双重防止死锁(有效的避免死锁问题,但是会带来新的问题)
     */
    public void lockV2ForCloseOrder() {
        String lockValue = System.currentTimeMillis()+expireTime+"";
        long isGetLock = RedisShardingPoolUtil.setnx("lockName",lockValue);
        if(isGetLock == 1) {//获取锁成功

            //为了防止死锁,调用锁的失效时间,  但是使用单层防死锁 ,进程A 获取了锁 但是没执行expire语句 ，会导致死锁
            RedisShardingPoolUtil.expire("lockName",expireTime);
            orderServiceImpl.closeTimeoutOrder(2);
            RedisShardingPoolUtil.del("lockName");
        }else{
            String lockValueA = RedisShardingPoolUtil.get("lockName");
            if(lockValueA!=null && System.currentTimeMillis()>Long.valueOf(lockValueA)) {//说明锁超时
                String lockValueB = RedisShardingPoolUtil.getset("lockName",System.currentTimeMillis()+expireTime+"");
                if(lockValueB  == null ||lockValueA.equals(lockValueB)) {
                    RedisShardingPoolUtil.expire("lockName",expireTime);
                    orderServiceImpl.closeTimeoutOrder(2);
                    RedisShardingPoolUtil.del("lockName");
                }else{
                    log.info("获取锁失败");
                }
            }else {
                log.info("获取锁失败");
            }
        }
    }

    public void lockV3ForCloseOrder() {
        String lock = SystemConst.REDIS_KEY.REDIS_LOCK_KEY;
        long requiredId = Thread.currentThread().getId();
        System.out.println("req"+requiredId);
        boolean tryGetLock = RedisLock.tryLock(lock,requiredId+"","NX","PX",5);
        try {
           if(tryGetLock){
               Thread delayLockExpire = new Thread(new DelayLockExpire(lock));
               delayLockExpire.start();
               orderServiceImpl.closeTimeoutOrder(2);
               delayLockExpire.stop();
           }
        } catch (Exception e) {
            log.error("执行分布式锁任务异常........{}",e);
        } finally {
            if(tryGetLock){
                //释放锁
                RedisLock.unlock(lock,requiredId+"");
            }
        }
    }

    class DelayLockExpire implements Runnable{
        private String lock;
        public DelayLockExpire(String lock ){
            this.lock =lock;
        }
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("守护线程开始执行");
                    Thread.sleep(4000);
                    if(RedisPoolUtil.isExists(lock)){
                        log.info("为锁续命.......");
                        System.out.println("锁:"+lock+":"+RedisPool.getJedis().ttl(lock));
                        RedisPoolUtil.expire(lock,5);
                        System.out.println("锁:"+lock+":"+RedisPool.getJedis().ttl(lock));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
