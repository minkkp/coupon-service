package com.pmk.coupon.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private final String code;
    private final String message;

    public static ApiResponse success(String message) {
        return new ApiResponse("SUCCESS", message);
    }

    public static ApiResponse fail(String code, String message) {
        return new ApiResponse(code, message);
    }
}