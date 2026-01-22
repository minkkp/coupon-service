package com.pmk.coupon.domain.event;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "total_coupon_count", nullable = false)
    private int totalCouponCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now();}

    public static Event create(
            String title,
            Long couponId,
            int totalCouponCount,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        Event event = new Event();
        event.title = title;
        event.couponId = couponId;
        event.totalCouponCount = totalCouponCount;
        event.startAt = startAt;
        event.endAt = endAt;
        event.status = EventStatus.READY;
        return event;
    }

    public void open() { this.status = EventStatus.OPEN;}

    public void close() {
        this.status = EventStatus.CLOSE;
    }

    public void updatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("startAt must be before endAt");
        }

        this.startAt = startAt;
        this.endAt = endAt;
    }
}