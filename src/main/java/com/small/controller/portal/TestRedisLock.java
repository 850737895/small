package com.small.controller.portal;

import com.small.common.SystemResponse;
import com.small.pojo.SMS;
import com.small.service.IOrderService;
import com.small.service.IRedisLockService;
import com.small.utils.JsonUtil;
import com.small.utils.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOError;
import java.util.UUID;

/**
 * Created by Administrator on 2018/6/13.
 */
@Controller
@RequestMapping("/redisLockController")
public class TestRedisLock {

    @Autowired
    private IRedisLockService redisLockServiceImpl;
    @Autowired
    private IOrderService orderServiceImpl;

    @RequestMapping("/lockV1.do")
    public void closeOrder(Long orderId) {
        redisLockServiceImpl.lockV1ForCloseOrder();
    }

    @RequestMapping("/lockV2.do")
    @ResponseBody
    public String closeOrder2() {
        for (int i=0;i<100;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    redisLockServiceImpl.lockV3ForCloseOrder();
                    //orderServiceImpl.closeTimeoutOrder(2);
                }
            }).start();
        }
        return "OK";
    }

    @RequestMapping("/testRest")
    @ResponseBody
    public SystemResponse testRest(@RequestBody String reqJson) {
        System.out.println(reqJson);
        return SystemResponse.createSuccessByMsg("成功");
    }

    @RequestMapping("/testReidsQueue.do")
    @ResponseBody
    public SystemResponse testRedisQueue() throws InterruptedException {
        while (true) {
            SMS sms = new SMS();
            sms.setReciver("13973550620");
            sms.setSmsContent(UUID.randomUUID().toString());
            RedisPoolUtil.lpush("sms_queue", JsonUtil.obj2Str(sms));
            System.out.println("生产者生产一条消息:" + sms.toString());
            Thread.sleep(100);
        }
    }
}
