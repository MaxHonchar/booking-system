package com.test.booking.listener;

import com.test.booking.service.IUnitDummyService;
import com.test.booking.service.IUnitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@AllArgsConstructor
@Component
public class AppReadyListener {

    private final IUnitDummyService dummyService;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("Add units on startup {}", ApplicationReadyEvent.class);
        final Instant start = Instant.now();
        log.info("Start processing units at {}", start);
        dummyService.initUnits();
        final Instant end = Instant.now();
        final Duration duration = Duration.between(start, end);
        log.info("End processing units at {}, for duration {}", end, duration);
    }

}
