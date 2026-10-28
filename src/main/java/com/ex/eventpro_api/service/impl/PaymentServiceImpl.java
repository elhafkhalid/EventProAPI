package com.ex.eventpro_api.service.impl;

import com.ex.eventpro_api.dto.request.PaymentRequest;
import com.ex.eventpro_api.dto.response.PaymentResponse;
import com.ex.eventpro_api.entity.Payment;
import com.ex.eventpro_api.entity.Reservation;
import com.ex.eventpro_api.enums.PaymentStatus;
import com.ex.eventpro_api.enums.ReservationStatus;
import com.ex.eventpro_api.exception.IdempotencyConflictException;
import com.ex.eventpro_api.exception.ResourceNotFoundException;
import com.ex.eventpro_api.mapper.PaymentMapper;
import com.ex.eventpro_api.repository.PaymentRepository;
import com.ex.eventpro_api.repository.ReservationRepository;
import com.ex.eventpro_api.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ReservationRepository reservationRepository;

    @Transactional
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        // 1. Vérifier l'idempotence
        Optional<PaymentResponse> existingResponse = checkIdempotency(request);
        if (existingResponse.isPresent()) {
            return existingResponse.get();
        }

        // 2. Récupérer la réservation
        Reservation reservation = getReservation(request.reservationId(), request.clientId());

        // 3. Valider le montant
        validateAmount(reservation, request.amount());

        // 4. Valider le statut et l'expiration
        validateReservationStatus(reservation);

        // 5. Créer le paiement
        Payment payment = createPaymentEntity(request, reservation);

        // 6. Confirmer la réservation
        confirmReservation(reservation);

        // 7. Sauvegarder le paiement et retourner la réponse
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }

    // ==========================================
    // MÉTHODES PRIVÉES
    // ==========================================

    private Optional<PaymentResponse> checkIdempotency(PaymentRequest request) {
        Optional<Payment> existingPaymentOpt = paymentRepository.findByIdempotencyKey(request.idempotencyKey());

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();

            // Vérification avec compareTo pour BigDecimal
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

    private Reservation getReservation(Long reservationId, Long clientId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        if (!reservation.getClient().getId().equals(clientId))
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à payer cette réservation. Elle appartient à un autre client.");

        return reservation;

    }

    private void validateAmount(Reservation reservation, BigDecimal requestedAmount) {
        if (reservation.getTotalAmount().compareTo(requestedAmount) != 0) {
            throw new IllegalArgumentException("Le montant envoyé (" + requestedAmount + ") ne correspond pas au total de la réservation (" + reservation.getTotalAmount() + ").");
        }
    }

    private void validateReservationStatus(Reservation reservation) {
        if (!reservation.getStatus().equals(ReservationStatus.PENDING_PAYMENT)
                || reservation.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette réservation est expirée ou n'est plus en attente de paiement.");
        }
    }

    private Payment createPaymentEntity(PaymentRequest request, Reservation reservation) {
        return Payment.builder()
                .reservation(reservation)
                .idempotencyKey(request.idempotencyKey())
                .reference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.amount())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();
    }

    private void confirmReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }
}