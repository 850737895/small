package com.small.task;

import com.small.common.RedissonManager;
import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.service.IOrderService;
import com.small.utils.PropertiesUtil;
import com.small.utils.RedisShardingPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 定时关单任务
 * Created by 85073 on 2018/6/8.
 */
@Slf4j
@Component
public class CloseOrderTask {

    @Autowired
    private IOrderService orderServiceImpl;

    @Autowired
    private RedissonManager redissonManager;

    /**
     * 集群情况下的任务调度(没有带分布式锁)
     */
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void closeTimeoutOrderV1() {
        log.info("定时关单任务启动");
        orderServiceImpl.closeTimeoutOrder(Integer.valueOf(PropertiesUtil.getPropertyValues("close.order.time","1")));
        log.info("定时关单任务完成");
    }

    /**
     * 集群环境下的单重防死锁。
     */
    //@Scheduled(cron = "0 0/1 * * *  ?")
    public void closeTimeoutOrderV2() {
        log.info("定时关单定时任务启动..........单重防死锁");

        //redis分布式锁的失效时间(秒为单位)
        int redislockExpireTime =Integer.valueOf(PropertiesUtil.getPropertyValues("redis.lock.key.time","5"));
        //分布式锁的value值(当前时间+redis锁失效时间)
        long lockValue = System.currentTimeMillis()+redislockExpireTime*1000;
        long lockFlag = RedisShardingPoolUtil.setnx(SystemConst.REDIS_KEY.REDIS_LOCK_KEY,lockValue+"");

        if(lockFlag == 1) {//获取到redis锁
            log.info("线程:{}获取到redis锁......开始执行定时任务",Thread.currentThread().getName());
            closeOrder(redislockExpireTime);
        }else {
            log.info("线程:{}没获取到redis锁.....",Thread.currentThread().getName());
        }
        log.info("定时关单定时任务结束..........单重防死锁");
    }

    /**
     * 集群环境下的双层防死锁。
     */
    //@Scheduled(cron = "0 0/1 * * *  ?")
    public void closeTimeoutOrderV3() {
        log.info("定时关单定时任务启动..........单重防死锁");

        //redis分布式锁的失效时间(秒为单位)
        int redislockExpireTime =Integer.valueOf(PropertiesUtil.getPropertyValues("redis.lock.key.time","5"));
        //分布式锁的value值(当前时间+redis锁失效时间)
        long lockValue = System.currentTimeMillis()+redislockExpireTime*1000;
        long lockFlag = RedisShardingPoolUtil.setnx(SystemConst.REDIS_KEY.REDIS_LOCK_KEY,lockValue+"");

        if(lockFlag == 1) {//获取到redis锁
            log.info("线程:{}获取到redis锁......开始执行定时任务",Thread.currentThread().getName());
            closeOrder(redislockExpireTime);
        }else {//没有或者到锁,进行下一步判断,看是否可以重置锁
            String lockValueA  = RedisShardingPoolUtil.get(SystemConst.REDIS_KEY.REDIS_LOCK_KEY);
            //第二重防止死锁,lockValueA  不为空，且 System.currentTimeMillis()>Long.valueOf(lockValueA)
            //表示说明redis锁 由于某种原因没有调用expire 或者del  已经是无效锁l
            //进一步判断是否需要重置锁
            if(lockValueA!=null && System.currentTimeMillis()>Long.valueOf(lockValueA)) {
                String lockValueB = RedisShardingPoolUtil.getset(SystemConst.REDIS_KEY.REDIS_LOCK_KEY,System.currentTimeMillis()+"");
                //lockValueB==null 表示原来的锁已经失效,
                //lockValueB!=null && StringUtils.equals(lockValueA,lockValueB))  表示  别的进程没有修改锁
                if(lockValueB == null || (lockValueB!=null && StringUtils.equals(lockValueA,lockValueB))) {
                    //本进程获取到锁
                    log.info("线程:{}获取到redis锁......开始执行定时任务",Thread.currentThread().getName());
                    closeOrder(redislockExpireTime);
                }else {
                    log.info("线程:{}没获取到redis锁.....",Thread.currentThread().getName());
                }
            }else {
                log.info("线程:{}没获取到redis锁.....",Thread.currentThread().getName());
            }
        }
        log.info("定时关单定时任务结束..........双重防死锁");
    }

    /**
     * 使用redisson构建分布式锁
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void closeTimeoutOrderV4() {
        log.info("定时关单定时任务启动..........单重防死锁");
        Redisson redisson = redissonManager.getRedisson();
        boolean isGetLock = false;
        RLock lock =redisson.getLock(SystemConst.REDIS_KEY.REDIS_LOCK_KEY);
        int waitTime = Integer.valueOf(PropertiesUtil.getPropertyValues("redisson.lock.wait.time","0"));
        int releaseTime = Integer.valueOf(PropertiesUtil.getPropertyValues("redisson.lock.release.time","50"));

        try {
            //获取到分布式锁
            if(isGetLock = lock.tryLock(waitTime,releaseTime, TimeUnit.SECONDS)) {
                log.info("获取到分布式锁:{}",SystemConst.REDIS_KEY.REDIS_LOCK_KEY);
                //orderServiceImpl.closeTimeoutOrder(Integer.valueOf(PropertiesUtil.getPropertyValues("close.order.time","1")));
            }else {
                log.info("没有获取到分布式锁........");
            }
        } catch (InterruptedException e) {
            log.info("分布式锁构建失败{}",e);
        }finally {
            if(!isGetLock) {
                return ;
            }
            lock.unlock();
        }

    }



    private void closeOrder(int redislockExpireTime) {
        //防止死锁，设置key的有效时间
        RedisShardingPoolUtil.expire(SystemConst.REDIS_KEY.REDIS_LOCK_KEY,redislockExpireTime);
        //orderServiceImpl.closeTimeoutOrder(Integer.valueOf(PropertiesUtil.getPropertyValues("close.order.time","1")));
        //若定时任务很轻的时候，减少redis锁资源占用
        RedisShardingPoolUtil.del(SystemConst.REDIS_KEY.REDIS_LOCK_KEY);
    }
}
