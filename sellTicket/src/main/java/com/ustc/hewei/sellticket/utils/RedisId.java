package com.ustc.hewei.sellticket.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author hewei
 * @version 1.0
 * @description: TODO
 * @date 2022/10/21 14:46
 */

@Component
public class RedisId {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    //2022年10月24号 11：22
    private static final long BEGIN_STAMP = 1666581726L;
    private static final int ORDER_BITS = 32;

    public long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_STAMP;

        //redis自增，并采用年月日的方式作为键
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long count = stringRedisTemplate.opsForValue().increment( keyPrefix + ":" + date);

        return timeStamp << ORDER_BITS | count;
    }
}
