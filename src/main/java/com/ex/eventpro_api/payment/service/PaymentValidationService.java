package com.ex.eventpro_api.payment.service;

import com.ex.eventpro_api.payment.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.dto.response.PaymentResponse;
import com.ex.eventpro_api.reservation.entity.Reservation;

import java.util.Optional;

public interface PaymentValidationService {

    // Vérifie l'idempotence et renvoie le reçu si c'est un doublon
    Optional<PaymentResponse> checkIdempotency(PaymentRequest request);

    // Récupère la réservation et vérifie le montant + le statut
    Reservation getAndValidateReservation(PaymentRequest request);
}