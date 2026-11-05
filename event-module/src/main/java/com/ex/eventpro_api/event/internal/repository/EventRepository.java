package com.ex.eventpro_api.event.internal.repository;

import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
}