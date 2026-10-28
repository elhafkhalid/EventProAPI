package com.ex.eventpro_api.controller;

import com.ex.eventpro_api.dto.request.ReservationRequest;
import com.ex.eventpro_api.dto.response.ReservationResponse;
import com.ex.eventpro_api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Renvoie 201 Created
    public ReservationResponse createReservation(@RequestBody ReservationRequest request) {
        return reservationService.createReservation(request);
    }
}