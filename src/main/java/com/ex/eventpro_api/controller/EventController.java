package com.ex.eventpro_api.controller;

import com.ex.eventpro_api.dto.request.EventCreateRequest;
import com.ex.eventpro_api.dto.response.EventResponse;
import com.ex.eventpro_api.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Endpoint POST pour créer un événement
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Renvoie le code 201 (Created)
    public EventResponse createEvent(@RequestBody EventCreateRequest request) {
        return eventService.createEvent(request);
    }
}
