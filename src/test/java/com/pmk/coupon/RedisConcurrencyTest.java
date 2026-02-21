package com.pmk.coupon;

import com.pmk.coupon.domain.couponissue.CouponIssueResult;
import com.pmk.coupon.domain.event.Event;
import com.pmk.coupon.domain.event.EventRepository;
import com.pmk.coupon.service.couponissue.CouponFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled("test")
class RedisConcurrencyTest {

    @Autowired
    private CouponFacadeService couponFacadeService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Long EVENT_ID = 1L;

    @BeforeEach
    void setUp() {
        Event event = eventRepository.findById(1L).orElseThrow();

        event.open(); // status = OPEN
        event.updatePeriod(
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(10)
        );

        eventRepository.save(event);

        // Redis 초기화
        redisTemplate.opsForValue().set("event:1:stock", "10");
        redisTemplate.delete("event:1:users");
    }

    @Test
    void test() throws InterruptedException {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger soldOut = new AtomicInteger();
        AtomicInteger duplicate = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1;

            executorService.submit(() -> {
                try {
                    CouponIssueResult result =
                            couponFacadeService.issue(userId, EVENT_ID);

                    switch (result) {
                        case SUCCESS -> success.incrementAndGet();
                        case SOLD_OUT -> soldOut.incrementAndGet();
                        case ALREADY_PARTICIPATED -> duplicate.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("SUCCESS = " + success.get());
        System.out.println("SOLD_OUT = " + soldOut.get());
        System.out.println("DUPLICATE = " + duplicate.get());

        assertThat(success.get()).isEqualTo(10);
        assertThat(duplicate.get()).isEqualTo(0);
        assertThat(success.get() + soldOut.get()).isEqualTo(100);
    }
}