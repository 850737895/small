package com.small.common;

import com.small.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * redisson 框架管理类
 * Created by 85073 on 2018/6/10.
 */
@Component
@Slf4j
public class RedissonManager {

    private Redisson redisson;

    public Redisson getRedisson() {
        return redisson;
    }

    /**IP*/
    private static String redis_server_ip= PropertiesUtil.getPropertyValues("redis.server.ip","47.104.128.12");
    /**port*/
    private static Integer redis_server_port = Integer.parseInt(PropertiesUtil.getPropertyValues("redis.server.port","6379"));

    /**
     * 调用构造器后
     */
    @PostConstruct
    private void init() {
        try {
            Config config = new Config();
            config.useSingleServer().setAddress(new StringBuffer().append(redis_server_ip).append(":").append(redis_server_port).toString());
            redisson = (Redisson) Redisson.create(config);
        } catch (Exception e) {
            log.info("初始化redisson对象异常{}",e);
        }
    }

}
