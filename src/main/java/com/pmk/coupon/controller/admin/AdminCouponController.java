package com.pmk.coupon.controller.admin;

import com.pmk.coupon.domain.coupon.Coupon;
import com.pmk.coupon.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class AdminCouponController {
    private final CouponService couponService;

    @PostMapping
    public Long create(@RequestBody Coupon coupon) { return couponService.create(coupon);}

    @GetMapping
    public List<Coupon> findAll() { return couponService.findAll(); }
}
