package com.choi.doit.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void redisOpsForHash(String key, Map<String, Object> value, int expire_h) {

        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, value);
        redisTemplate.expire(key, expire_h, TimeUnit.HOURS);

        log.info("[Redis] Data saved successfully. -- " + key);
    }
}
