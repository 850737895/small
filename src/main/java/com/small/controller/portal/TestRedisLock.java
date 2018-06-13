package com.small.controller.portal;

import com.small.service.IOrderService;
import com.small.service.IRedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2018/6/13.
 */
@Controller("/redisLockController")
public class TestRedisLock {

    @Autowired
    private IRedisLockService redisLockServiceImpl;

    @RequestMapping("/lockV1")
    public void closeOrder(Long orderId) {
        redisLockServiceImpl.lockV1ForCloseOrder();
    }
}
