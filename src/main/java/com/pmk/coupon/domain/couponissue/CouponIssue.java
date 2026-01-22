package com.pmk.coupon.domain.couponissue;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon_issue",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "event_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponIssueStatus status;

    protected CouponIssue(Long userId, Long eventId, CouponIssueStatus status) {
        this.userId = userId;
        this.eventId = eventId;
        this.issuedAt = LocalDateTime.now();
        this.status = status;
    }

    public static CouponIssue issued(Long userId, Long eventId) {
        return new CouponIssue(userId, eventId, CouponIssueStatus.ISSUED);
    }
}