package com.pmk.coupon.service.couponissue;

import com.pmk.coupon.domain.couponissue.CouponIssueResult;
import com.pmk.coupon.service.event.EventService;
import com.pmk.coupon.service.redis.RedisCouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponFacadeServiceTest {

    @Mock
    private RedisCouponService redisCouponService;

    @Mock
    private AsyncCouponIssueService asyncCouponIssueService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private CouponFacadeService couponFacadeService;

    @Test
    void 발급성공시_async저장호출() {

        // given
        given(redisCouponService.issue(100L, 1L))
                .willReturn(CouponIssueResult.SUCCESS);

        // when
        CouponIssueResult result =
                couponFacadeService.issue(1L, 100L);

        // then
        assertThat(result).isEqualTo(CouponIssueResult.SUCCESS);

        verify(eventService).validateIssuable(100L);
        verify(asyncCouponIssueService).save(1L, 100L);
    }

    @Test
    void 소진시_async저장호출안함() {

        given(redisCouponService.issue(100L, 1L))
                .willReturn(CouponIssueResult.SOLD_OUT);

        CouponIssueResult result =
                couponFacadeService.issue(1L, 100L);

        assertThat(result).isEqualTo(CouponIssueResult.SOLD_OUT);

        verify(eventService).validateIssuable(100L);
        verify(asyncCouponIssueService, never()).save(any(), any());
    }
}
