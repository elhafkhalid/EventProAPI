package com.ex.eventpro_api.payment.service;

import com.ex.eventpro_api.payment.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
}
