package com.pmk.coupon.domain.couponissue;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponIssueRequest {
    private Long userId;
    private Long eventId;
}