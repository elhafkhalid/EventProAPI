package com.ex.eventpro_api.service;

import com.ex.eventpro_api.dto.request.ReservationRequest;
import com.ex.eventpro_api.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
}
