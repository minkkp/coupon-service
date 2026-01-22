package com.pmk.coupon.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisEventInitializer {

    private final StringRedisTemplate redisTemplate;

    // 이벤트 재고 초기화 및 TTL 설정
    public void init(Long eventId, int totalCouponCount, Duration ttl) {
        redisTemplate.opsForValue().set(stockKey(eventId), String.valueOf(totalCouponCount), ttl);
        redisTemplate.delete(userKey(eventId));
    }

    // 이벤트 종료 시 Redis 키 제거
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
