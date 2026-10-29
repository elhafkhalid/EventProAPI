package com.ex.eventpro_api.payment.service.Impl;

import com.ex.eventpro_api.core.exception.IdempotencyConflictException;
import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.payment.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.entity.Payment;
import com.ex.eventpro_api.payment.mapper.PaymentMapper;
import com.ex.eventpro_api.payment.repository.PaymentRepository;
import com.ex.eventpro_api.payment.service.PaymentValidationService;
import com.ex.eventpro_api.reservation.entity.Reservation;
import com.ex.eventpro_api.reservation.enums.ReservationStatus;
import com.ex.eventpro_api.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentValidationServiceImpl implements PaymentValidationService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Optional<PaymentResponse> checkIdempotency(PaymentRequest request) {
        Optional<Payment> existingPaymentOpt = paymentRepository.findByIdempotencyKey(request.idempotencyKey());

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();

            // On utilise compareTo pour BigDecimal !
            boolean isSamePayload = existingPayment.getReservation().getId().equals(request.reservationId())
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
    public Reservation getAndValidateReservation(PaymentRequest request) {
        // 1. Récupérer
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        // 2. Valider le montant
        if (reservation.getTotalAmount().compareTo(request.amount()) != 0) {
            throw new IllegalArgumentException("Le montant envoyé (" + request.amount() + ") ne correspond pas au total de la réservation (" + reservation.getTotalAmount() + ").");
        }

        // 3. Valider le statut et l'expiration
        if (!reservation.getStatus().equals(ReservationStatus.PENDING_PAYMENT)
                || reservation.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette réservation est expirée ou n'est plus en attente de paiement.");
        }

        return reservation;
    }
}