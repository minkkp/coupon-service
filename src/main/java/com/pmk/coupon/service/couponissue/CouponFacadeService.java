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

    public CouponIssueResult issue(Long userId, Long eventId) {

        eventService.validateIssuable(eventId);

        CouponIssueResult result = redisCouponService.issue(eventId, userId);

        if (result == CouponIssueResult.SUCCESS) {
            asyncCouponIssueService.save(userId, eventId);
        }

        return result;
    }
}