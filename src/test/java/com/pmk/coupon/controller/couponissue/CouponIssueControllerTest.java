package com.pmk.coupon.controller.couponissue;

import com.pmk.coupon.domain.couponissue.CouponIssueResult;
import com.pmk.coupon.service.couponissue.CouponFacadeService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponIssueController.class)
@AutoConfigureMockMvc(addFilters = false)
class CouponIssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponFacadeService couponFacadeService;

    @Test
    void 쿠폰발급_성공() throws Exception {

        given(couponFacadeService.issue(1L, 100L))
                .willReturn(CouponIssueResult.SUCCESS);

        String json = """
            {
              "userId": 1,
              "eventId": 100
            }
        """;

        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("쿠폰 발급 성공"));

        verify(couponFacadeService).issue(1L, 100L);
    }

    @Test
    void 쿠폰발급_소진() throws Exception {

        given(couponFacadeService.issue(1L, 100L))
                .willReturn(CouponIssueResult.SOLD_OUT);

        String json = """
                    {
                      "userId": 1,
                      "eventId": 100
                    }
                """;

        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SOLD_OUT"))
                .andExpect(jsonPath("$.message").value("쿠폰 소진"));
    }
}