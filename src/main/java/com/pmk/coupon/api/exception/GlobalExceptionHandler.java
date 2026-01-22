package com.pmk.coupon.api.exception;


import com.pmk.coupon.api.response.ApiResponse;
import com.pmk.coupon.global.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusiness(BusinessException e) {

        if ("REDIS_UNAVAILABLE".equals(e.getMessage())) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.fail(
                            "REDIS_UNAVAILABLE",
                            "쿠폰 발급이 일시적으로 중단되었습니다"
                    ));
        }

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("INVALID_EVENT", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleEtc(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("INTERNAL_ERROR", "서버 오류"));
    }
}