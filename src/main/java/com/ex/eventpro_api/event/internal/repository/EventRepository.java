package com.ex.eventpro_api.event.internal.repository;

import com.ex.eventpro_api.event.internal.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}