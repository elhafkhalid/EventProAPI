package com.ex.eventpro_api.payment.internal.service.impl;

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

    // On utilise la Façade !
    private final ReservationFacade reservationFacade;

    @Transactional
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        // 1. Vérifier l'idempotence
        Optional<PaymentResponse> existingResponse = validationService.checkIdempotency(request);
        if (existingResponse.isPresent()) {
            return existingResponse.get();
        }

        // 2. Valider la réservation (montant + statut)
        ReservationDto reservation = validationService.getAndValidateReservation(request);

        // 3. Créer le paiement
        Payment payment = Payment.builder()
                .reservationId(reservation.id()) // On met l'ID direct !
                .idempotencyKey(request.idempotencyKey())
                .reference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.amount())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();

        // 4. Confirmer la réservation via la Façade
        reservationFacade.confirmReservation(reservation.id());

        // 5. Sauvegarder le paiement et renvoyer le reçu
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }
}