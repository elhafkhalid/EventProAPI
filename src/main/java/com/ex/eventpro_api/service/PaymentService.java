package com.ex.eventpro_api.service;

import com.ex.eventpro_api.dto.request.PaymentRequest;
import com.ex.eventpro_api.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
}
