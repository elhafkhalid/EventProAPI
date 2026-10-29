package com.ex.eventpro_api.payment.controller;

import com.ex.eventpro_api.payment.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.dto.response.PaymentResponse;
import com.ex.eventpro_api.payment.service.PaymentService;
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
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}