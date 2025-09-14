package com.test.booking.service;

import com.test.booking.domain.Event;
import com.test.booking.enums.EventType;

import java.util.Optional;

public interface IEventService {
    Optional<Event> findById(Long id);
    Optional<Event> findByType(EventType type);
}
