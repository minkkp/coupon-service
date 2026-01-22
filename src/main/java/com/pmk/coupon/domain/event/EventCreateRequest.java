package com.pmk.coupon.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EventCreateRequest {
    private String title;
    private Long couponId;
    private int totalCouponCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}