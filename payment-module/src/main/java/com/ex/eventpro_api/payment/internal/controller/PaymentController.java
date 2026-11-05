package com.ex.eventpro_api.payment.internal.controller;

import com.ex.eventpro_api.payment.internal.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.internal.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.internal.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse processPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @GetMapping("/reservation/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse getPaymentByReservation(@PathVariable Long reservationId) {
        return paymentService.getPaymentByReservationId(reservationId);
    }
}