package com.pmk.coupon.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisEventInitializer {

    private final StringRedisTemplate redisTemplate;

    public void init(Long eventId, int totalCouponCount, Duration ttl) {
        redisTemplate.opsForValue().set(stockKey(eventId), String.valueOf(totalCouponCount), ttl);
        redisTemplate.delete(userKey(eventId));
    }

    public void clear(Long eventId) {
        redisTemplate.delete(stockKey(eventId));
        redisTemplate.delete(userKey(eventId));
    }

    private String stockKey(Long eventId){
        return "event:" + eventId + ":stock";
    }

    private String userKey(Long eventId){
        return "event:" + eventId + ":users";
    }

}
