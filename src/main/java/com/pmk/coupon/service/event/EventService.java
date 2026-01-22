package com.pmk.coupon.service.event;

import com.pmk.coupon.domain.event.Event;
import com.pmk.coupon.domain.event.EventCreateRequest;
import com.pmk.coupon.domain.event.EventRepository;
import com.pmk.coupon.domain.event.EventStatus;
import com.pmk.coupon.global.BusinessException;
import com.pmk.coupon.service.couponissue.CouponIssueService;
import com.pmk.coupon.service.redis.RedisEventInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RedisEventInitializer redisEventInitializer;
    private final CouponIssueService couponIssueService;

    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("이벤트 없음"));
    }

    @Transactional
    public Event createEvent(EventCreateRequest request) {

        Event event = Event.create(
                request.getTitle(),
                request.getCouponId(),
                request.getTotalCouponCount(),
                request.getStartAt(),
                request.getEndAt()
        );

        return eventRepository.save(event);
    }

    @Transactional
    public Event openEvent(Long eventId) {
        Event event = getEvent(eventId);

//        if (event.getStatus() != EventStatus.READY) {
//            throw new BusinessException("READY 상태만 오픈 가능");
//        }

        Duration ttl;
        ttl = Duration.between(
                LocalDateTime.now(),
                event.getEndAt()
        ).plusMinutes(1);

        event.open();
        redisEventInitializer.init(eventId, event.getTotalCouponCount(), ttl);
        return event;
    }

    @Transactional(readOnly = true)
    public void syncEvent(Long eventId) {
        Event event = getEvent(eventId);

        int issuedCount = couponIssueService.countIssuedByEvent(eventId);
        int remaining = event.getTotalCouponCount() - issuedCount;

        Duration ttl = Duration.between(
                LocalDateTime.now(),
                event.getEndAt()
        ).plusMinutes(1);

        redisEventInitializer.init(eventId, remaining, ttl);
    }

    @Transactional
    public Event closeEvent(Long eventId) {
        Event event = getEvent(eventId);

        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessException("OPEN 상태만 종료 가능");
        }

        event.close();
        redisEventInitializer.clear(eventId);
        return event;
    }

    @Transactional
    public void updatePeriod(Long eventId, LocalDateTime start, LocalDateTime end) {
        Event event = getEvent(eventId);
        event.updatePeriod(start, end);
    }

    @Transactional(readOnly = true)
    public Event validateIssuable(Long eventId) {
        Event event = getEvent(eventId);

        LocalDateTime now = LocalDateTime.now();

        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessException("이벤트 오픈 아님");
        }

        if (now.isBefore(event.getStartAt()) || now.isAfter(event.getEndAt())) {
            throw new BusinessException("이벤트 시간 아님");
        }

        return event;
    }

}