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

    /**
     * 쿠폰 발급 성공 이력을 비동기로 DB에 저장
     * - 쿠폰 발급 API 응답 시간을 줄이기 위해 DB 작업을 비동기로 분리
     * - 실패 시 재시도 없이 운영자가 syncEvent API로 복구
     */
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