package com.ex.eventpro_api.service;

import com.ex.eventpro_api.dto.request.EventCreateRequest;
import com.ex.eventpro_api.dto.response.EventResponse;

public interface EventService {
    EventResponse createEvent(EventCreateRequest request);
}