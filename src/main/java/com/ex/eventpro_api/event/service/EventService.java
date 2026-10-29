package com.ex.eventpro_api.event.service;

import com.ex.eventpro_api.event.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.dto.response.EventResponse;

public interface EventService {
    EventResponse createEvent(EventCreateRequest request);
}