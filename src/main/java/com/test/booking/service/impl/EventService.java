package com.test.booking.service.impl;

import com.test.booking.domain.Event;
import com.test.booking.repository.IEventRepository;
import com.test.booking.service.IEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class EventService implements IEventService {

    private final IEventRepository eventRepository;

    @Override
    public Optional<Event> findById(Long id) {
        log.debug("find Event by id {}", id);
        return eventRepository.findById(id);
    }
}
