package com.small.service.impl;

import com.small.service.IOrderService;
import com.small.service.IRedisLockService;
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

    @Autowired
    private IOrderService orderServiceImpl;

    @Override
    public void lockV1ForCloseOrder() {
        String lockValue = System.currentTimeMillis()+10*60*1000+"";
        long isGetLock = RedisShardingPoolUtil.setnx("lockName",lockValue);
        if(isGetLock == 1) {//获取锁成功

            //为了防止死锁,调用锁的失效时间,  但是使用单层防死锁 ,进程A 获取了锁 但是没执行expire语句 ，会导致死锁
            RedisShardingPoolUtil.expire("lockName",10*60*1000);
            orderServiceImpl.closeTimeoutOrder(2);
        }else{
            log.info("获取锁失败");
        }
    }
}
