package com.ex.eventpro_api.payment.internal.service;

import com.ex.eventpro_api.payment.internal.dto.request.PaymentRequest;
import com.ex.eventpro_api.payment.internal.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
}
