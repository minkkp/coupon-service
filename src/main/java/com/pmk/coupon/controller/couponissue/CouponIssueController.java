package com.pmk.coupon.controller.couponissue;

import com.pmk.coupon.api.response.ApiResponse;
import com.pmk.coupon.domain.couponissue.CouponIssueRequest;
import com.pmk.coupon.domain.couponissue.CouponIssueResult;
import com.pmk.coupon.service.couponissue.CouponFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponIssueController {

    private final CouponFacadeService couponFacadeService;

    @PostMapping("/issue")
    public ApiResponse issue(@RequestBody CouponIssueRequest request)
    {
        CouponIssueResult result = couponFacadeService.issue(request.getUserId(), request.getEventId());

        return switch (result) {
            case SUCCESS ->
                    ApiResponse.success("쿠폰 발급 성공");
            case SOLD_OUT ->
                    ApiResponse.fail("SOLD_OUT", "쿠폰 소진");
            case ALREADY_PARTICIPATED ->
                    ApiResponse.fail("ALREADY_PARTICIPATED", "이미 참여한 이벤트");
        };
    }
}