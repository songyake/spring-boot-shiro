package com.test.springboot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Author: songyake
 * @Date: 2019/05/22
 */

@Component
@Slf4j
public class RedisDistributeLock{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public RedisDistributeLock() {
    }

    public boolean tryLock(String requestId, String key, int expireTime) {
        try {
            String result = stringRedisTemplate.execute((RedisCallback<String>) connection -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                return commands.set(key, requestId, "NX", "PX", expireTime);
            });
            return !StringUtils.isEmpty(result);
        } catch (Exception var5) {
            log.error("redis异常", var5);
            return false;
        }
    }

    public boolean getLock(String requestId, String key, int expireTime, int maxRetryTime) {
        int retryTime = 0;

        while(!this.tryLock(requestId, key, expireTime)) {
            if (maxRetryTime <= 0 || retryTime > maxRetryTime) {
                return false;
            }

            retryTime += 10;

            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException var7) {
                log.error("睡眠中断异常", var7);
            }
        }

        return true;
    }

    public boolean getLock(String requestId, String key, int expireTime) {
        return this.getLock(requestId, key, expireTime, 0);
    }

    public boolean releaseLock(String requestId, String key) {
        final String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
        // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
        Long result = (Long)this.stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
            Object nativeConnection = connection.getNativeConnection();
            // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
            // 集群模式
            if (nativeConnection instanceof JedisCluster) {
                return (Long) ((JedisCluster) nativeConnection).eval(script, Arrays.asList(key), Arrays.asList(requestId));
            }
            // 单机模式
            else if (nativeConnection instanceof Jedis) {
                return (Long) ((Jedis) nativeConnection).eval(script, Arrays.asList(key), Arrays.asList(requestId));
            }
            return 0L;
        });
        return result != null && result > 0;
    }
}