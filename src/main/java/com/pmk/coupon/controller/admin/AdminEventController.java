package com.pmk.coupon.controller.admin;

import com.pmk.coupon.domain.event.EventCreateRequest;
import com.pmk.coupon.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @PostMapping
    public void createEvent(@RequestBody EventCreateRequest request) { eventService.createEvent(request);}

    @PostMapping("/open/{eventId}")
    public void openEvent(@PathVariable Long eventId) {
        eventService.openEvent(eventId);
    }

    @PostMapping("/close/{eventId}")
    public void closeEvent(@PathVariable Long eventId) {
        eventService.closeEvent(eventId);
    }

    @PostMapping("/sync/{eventId}")
    public void syncEvent(@PathVariable Long eventId) {
        eventService.syncEvent(eventId);
    }

    @PostMapping("/period/{eventId}")
    public void updatePeriod(@PathVariable Long eventId) {
        //test
        eventService.updatePeriod(eventId, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(10));
    }
}