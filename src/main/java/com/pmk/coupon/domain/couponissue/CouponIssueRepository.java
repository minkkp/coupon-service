package com.pmk.coupon.domain.couponissue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    int countByEventId(Long eventId);

}