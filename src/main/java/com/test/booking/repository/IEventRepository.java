package com.test.booking.repository;

import com.test.booking.domain.Event;
import com.test.booking.enums.EventType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEventRepository extends CrudRepository<Event, Long> {
    Optional<Event> findEventByType(EventType type);
}
