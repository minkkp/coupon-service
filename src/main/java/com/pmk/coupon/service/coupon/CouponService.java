package com.pmk.coupon.service.coupon;

import com.pmk.coupon.domain.coupon.Coupon;
import com.pmk.coupon.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Long create(Coupon coupon) {
        return couponRepository.save(coupon).getId();
    }

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }
}