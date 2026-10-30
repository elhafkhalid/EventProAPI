package com.ex.eventpro_api.payment.internal.service;

import com.ex.eventpro_api.payment.internal.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.internal.dto.response.PaymentResponse;
import com.ex.eventpro_api.reservation.api.dto.ReservationDto;

import java.util.Optional;

public interface PaymentValidationService {

    // Vérifie l'idempotence et renvoie le reçu si c'est un doublon
    Optional<PaymentResponse> checkIdempotency(PaymentRequest request);

    // Récupère la réservation et vérifie le montant + le statut
    ReservationDto getAndValidateReservation(PaymentRequest request);
}