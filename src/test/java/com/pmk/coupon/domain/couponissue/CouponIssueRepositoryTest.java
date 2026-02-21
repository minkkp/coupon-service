package com.pmk.coupon.domain.couponissue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CouponIssueRepositoryTest {

    @Autowired
    private CouponIssueRepository repository;

    @Test
    void 이벤트별_발급수_카운트() {

        CouponIssue issue1 = CouponIssue.issued(1L, 100L);
        CouponIssue issue2 = CouponIssue.issued(2L, 100L);

        repository.save(issue1);
        repository.save(issue2);

        int count = repository.countByEventId(100L);

        assertThat(count).isEqualTo(2);
    }
}