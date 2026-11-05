package com.ex.eventpro_api.event.internal.service;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.response.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse createEvent(EventCreateRequest request);
    void publishEvent(Long eventId);
    List<EventResponse> getAllPublishedEvents();
    EventResponse getEventById(Long eventId);
}