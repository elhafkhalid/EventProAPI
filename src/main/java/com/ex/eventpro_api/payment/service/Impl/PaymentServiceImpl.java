package com.ex.eventpro_api.payment.service.Impl;

import com.ex.eventpro_api.payment.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.entity.Payment;
import com.ex.eventpro_api.payment.mapper.PaymentMapper;
import com.ex.eventpro_api.payment.repository.PaymentRepository;
import com.ex.eventpro_api.payment.service.PaymentService;
import com.ex.eventpro_api.payment.service.PaymentValidationService;
import com.ex.eventpro_api.reservation.entity.Reservation;
import com.ex.eventpro_api.reservation.enums.ReservationStatus;
import com.ex.eventpro_api.reservation.repository.ReservationRepository;
import com.ex.eventpro_api.payment.enums.PaymentStatus;
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
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    // On injecte le service de validation
    private final PaymentValidationService validationService;

    @Transactional
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        // 1. DÉLÉGATION : Vérifier l'idempotence
        Optional<PaymentResponse> existingResponse = validationService.checkIdempotency(request);
        if (existingResponse.isPresent()) {
            return existingResponse.get();
        }

        // 2. DÉLÉGATION : Récupérer et valider la réservation
        Reservation reservation = validationService.getAndValidateReservation(request);

        // 3. Créer le paiement
        Payment payment = Payment.builder()
                .reservation(reservation)
                .idempotencyKey(request.idempotencyKey())
                .reference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(request.amount())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .build();

        // 4. Confirmer la réservation
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        // 5. Sauvegarder le paiement et renvoyer le reçu
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }
}