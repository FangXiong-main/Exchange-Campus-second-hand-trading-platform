package com.fangxiong.config;

import com.fangxiong.utils.redis.RedisUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisUtilsConfig {
    @Bean
    public RedisUtils redisUtils(StringRedisTemplate stringRedisTemplate) {
        return new RedisUtils(stringRedisTemplate);
    }
}
