package com.ex.eventpro_api.payment.internal.service.Impl;

import com.ex.eventpro_api.payment.internal.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.internal.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.internal.entity.Payment;
import com.ex.eventpro_api.payment.internal.enums.PaymentStatus;
import com.ex.eventpro_api.payment.internal.mapper.PaymentMapper;
import com.ex.eventpro_api.payment.internal.repository.PaymentRepository;
import com.ex.eventpro_api.payment.internal.service.PaymentService;
import com.ex.eventpro_api.payment.internal.service.PaymentValidationService;
import com.ex.eventpro_api.reservation.api.ReservationFacade;
import com.ex.eventpro_api.reservation.api.dto.ReservationDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentValidationService validationService;
    private final ReservationFacade reservationFacade;

    @Transactional
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        // 1. Vérifier l'idempotence (Si déjà payé, on renvoie le reçu)
        Optional<PaymentResponse> existingResponse = validationService.checkIdempotency(request);
        if (existingResponse.isPresent()) {
            return existingResponse.get();
        }

        // 2. Valider la réservation (montant, statut, expiration)
        ReservationDto reservationDto = validationService.getAndValidateReservation(request);

        // 3. Créer et sauvegarder le paiement
        Payment savedPayment = createAndSavePayment(request, reservationDto);

        // 4. Confirmer la réservation via la Façade
        confirmReservation(reservationDto);

        // 5. Renvoyer le reçu
        return paymentMapper.toResponse(savedPayment);
    }

    // ==========================================
    // MÉTHODES PRIVÉES
    // ==========================================

    private Payment createAndSavePayment(PaymentRequest request, ReservationDto reservationDto) {
        Payment payment = Payment.builder()
                .reservationId(reservationDto.id())
                .idempotencyKey(request.idempotencyKey())
                .reference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.amount())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PENDING) // On crée toujours en PENDING
                .build();

        // On utilise la méthode métier de l'entité pour passer à SUCCESS
        payment.markAsSuccess();

        return paymentRepository.save(payment);
    }

    private void confirmReservation(ReservationDto reservationDto) {
        // On demande au module Reservation de passer la réservation en CONFIRMED
        reservationFacade.confirmReservation(reservationDto.id());
    }
}