package com.pmk.coupon.controller.admin;

import com.pmk.coupon.service.event.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void openEvent() throws Exception {

        mockMvc.perform(post("/admin/events/open/1"))
                .andExpect(status().isOk());

        verify(eventService).openEvent(1L);
    }

    @Test
    void closeEvent() throws Exception {

        mockMvc.perform(post("/admin/events/close/1"))
                .andExpect(status().isOk());

        verify(eventService).closeEvent(1L);
    }
}