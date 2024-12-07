package com.hotsharp.utils;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void put (String key, Object value) {
        String v = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, v);
    }

    public void put (String key, Object value, long timeout, TimeUnit unit) {
        String v = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key, v, timeout, unit);
    }

    public <T> T get (String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSONUtil.toBean(value, clazz);
    }

    public String get (String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
