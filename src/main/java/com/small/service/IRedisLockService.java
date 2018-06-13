package com.small.service;

/**
 * Created by Administrator on 2018/6/13.
 */
public interface IRedisLockService {

    /**
     * 第一个版本单重防死锁功能
     */
    public void lockV1ForCloseOrder();
}
