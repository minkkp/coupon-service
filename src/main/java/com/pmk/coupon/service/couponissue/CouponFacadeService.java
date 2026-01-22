package com.pmk.coupon.service.couponissue;

import com.pmk.coupon.domain.couponissue.CouponIssueResult;
import com.pmk.coupon.service.event.EventService;
import com.pmk.coupon.service.redis.RedisCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponFacadeService {

    private final RedisCouponService redisCouponService;
    private final AsyncCouponIssueService asyncCouponIssueService;
    private final EventService eventService;

    /**
     * 쿠폰 발급 전체 흐름을 담당하는 Facade
     *
     * 설계 의도:
     * - 트래픽 진입 지점에서 흐름을 단순화
     * - 동시성 제어는 Redis, 정합성 기준은 DB로 명확히 분리
     *
     * 처리 순서:
     * 1. 이벤트 상태/기간 검증 (DB 기준)
     * 2. Redis + Lua로 선착순 발급 (원자 처리)
     * 3. 성공 케이스만 DB에 비동기 이력 저장
     */
    public CouponIssueResult issue(Long userId, Long eventId) {

        // 이벤트가 발급 가능한 상태인지 사전 검증
        eventService.validateIssuable(eventId);

        // Redis + Lua 기반 선착순 발급
        CouponIssueResult result = redisCouponService.issue(eventId, userId);

        // 성공한 경우에만 DB 이력 저장 (트래픽 경로에서 DB 제거)
        if (result == CouponIssueResult.SUCCESS) {
            asyncCouponIssueService.save(userId, eventId);
        }

        return result;
    }
}