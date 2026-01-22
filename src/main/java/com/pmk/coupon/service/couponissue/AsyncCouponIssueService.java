package com.pmk.coupon.service.couponissue;

import com.pmk.coupon.domain.couponissue.CouponIssue;
import com.pmk.coupon.domain.couponissue.CouponIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncCouponIssueService {

    private final CouponIssueRepository couponIssueRepository;

    @Async
    @Transactional
    public void save(Long userId, Long eventId) {
        try {
            CouponIssue issue = CouponIssue.issued(userId, eventId);
            couponIssueRepository.save(issue);
        } catch (Exception e) {
            log.error("CouponIssue save failed. userId={}, eventId={}", userId, eventId, e);
        }
    }
}