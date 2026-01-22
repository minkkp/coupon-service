package com.pmk.coupon.service.redis;

import com.pmk.coupon.domain.couponissue.CouponIssueResult;
import com.pmk.coupon.global.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCouponService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> couponIssueScript;

    public CouponIssueResult issue(Long eventId, Long userId) {
        try {
            String usersKey = usersKey(eventId);
            String stockKey = stockKey(eventId);

            Long result = redisTemplate.execute(
                    couponIssueScript,
                    List.of(usersKey, stockKey),
                    userId.toString()
            );

            if (result == null) {
                throw new BusinessException("Redis returned null");
            }

            return switch (result.intValue()) {
                case 1  -> CouponIssueResult.SUCCESS;
                case 0  -> CouponIssueResult.SOLD_OUT;
                case -1 -> CouponIssueResult.ALREADY_PARTICIPATED;
                default -> throw new BusinessException("Unexpected redis result: " + result);

            };

        } catch (Exception e) {
            log.warn("Redis issue failed. eventId={}, userId={}", eventId, userId, e);
            throw new BusinessException("REDIS_UNAVAILABLE");
        }
    }

    private String stockKey(Long eventId){
        return "event:" + eventId + ":stock";
    }

    private String usersKey(Long eventId){
        return "event:" + eventId + ":users";
    }
}