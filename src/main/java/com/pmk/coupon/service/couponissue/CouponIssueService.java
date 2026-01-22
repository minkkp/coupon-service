package com.pmk.coupon.service.couponissue;

import com.pmk.coupon.domain.couponissue.CouponIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CouponIssueRepository couponIssueRepository;

    @Transactional(readOnly = true)
    public int countIssuedByEvent(Long eventId) {
        return couponIssueRepository.countByEventId(eventId);
    }
}
