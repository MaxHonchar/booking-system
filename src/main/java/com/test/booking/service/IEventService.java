package com.test.booking.service;

import com.test.booking.domain.Event;

import java.util.Optional;

public interface IEventService {
    Optional<Event> findById(Long id);
}
