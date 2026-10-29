package com.ex.eventpro_api.payment.internal.service.Impl;

import com.ex.eventpro_api.core.exception.IdempotencyConflictException;
import com.ex.eventpro_api.payment.internal.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.internal.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.internal.entity.Payment;
import com.ex.eventpro_api.payment.internal.mapper.PaymentMapper;
import com.ex.eventpro_api.payment.internal.repository.PaymentRepository;
import com.ex.eventpro_api.payment.internal.service.PaymentValidationService;
import com.ex.eventpro_api.reservation.api.ReservationFacade;
import com.ex.eventpro_api.reservation.api.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentValidationServiceImpl implements PaymentValidationService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    // On utilise la Façade de Reservation !
    private final ReservationFacade reservationFacade;

    @Override
    public Optional<PaymentResponse> checkIdempotency(PaymentRequest request) {
        Optional<Payment> existingPaymentOpt = paymentRepository.findByIdempotencyKey(request.idempotencyKey());

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();

            boolean isSamePayload = existingPayment.getReservationId().equals(request.reservationId())
                    && existingPayment.getAmount().compareTo(request.amount()) == 0;

            if (isSamePayload) {
                return Optional.of(paymentMapper.toResponse(existingPayment));
            } else {
                throw new IdempotencyConflictException("Cette clé d'idempotence a déjà été utilisée pour une autre transaction.");
            }
        }
        return Optional.empty();
    }

    @Override
    public ReservationDto getAndValidateReservation(PaymentRequest request) {
        // 1. On récupère le DTO via la Façade (qui gère le 404 si introuvable)
        ReservationDto reservation = reservationFacade.getReservationById(request.reservationId());

        // --- AJOUTE LA VÉRIFICATION DE SÉCURITÉ ICI ---

        if (!reservation.clientId().equals(request.clientId())) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à payer cette réservation. Elle appartient à un autre client.");
        }

        // 2. Valider le montant
        if (reservation.totalAmount().compareTo(request.amount()) != 0) {
            throw new IllegalArgumentException("Le montant envoyé (" + request.amount() + ") ne correspond pas au total de la réservation (" + reservation.totalAmount() + ").");
        }

        // 3. Valider le statut et l'expiration (le statut est un String dans le DTO)
        if (!reservation.status().equals("PENDING_PAYMENT")
                || reservation.expirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette réservation est expirée ou n'est plus en attente de paiement.");
        }

        return reservation;
    }
}