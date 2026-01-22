package com.pmk.coupon.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthChecker implements ApplicationRunner {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            redisTemplate.opsForValue().set("redis:health", "ok");
            String value = redisTemplate.opsForValue().get("redis:health");

            if (!"ok".equals(value)) {
                throw new IllegalStateException("Redis health check failed");
            }

            log.info("Redis health check success");

        } catch (Exception e) {
            log.error("Redis health check failed", e);
            throw e;
        }
    }
}