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

    /**
     * Redis + Lua를 이용한 선착순 쿠폰 발급
     *
     * 설계 의도:
     * - 재고 감소, 중복 참여 체크를 Redis 단일 스크립트로 처리
     * - 동시 요청 환경에서도 과발급 방지
     * - Redis 장애 시 즉시 실패(Fail-Fast)하여 정합성 보장
     */
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
                case 1  -> CouponIssueResult.SUCCESS;              // 발급 성공
                case 0  -> CouponIssueResult.SOLD_OUT;             // 재고 소진
                case -1 -> CouponIssueResult.ALREADY_PARTICIPATED; // 중복 참여
                default -> throw new BusinessException("Unexpected redis result: " + result);

            };

        } catch (Exception e) {
            // Redis 장애 시 재시도/DB fallback 없이 즉시 실패
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