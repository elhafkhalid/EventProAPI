package com.ex.eventpro_api.payment.dto.request;

import java.math.BigDecimal;

public record PaymentRequest(
        Long clientId,
        Long reservationId,
        String idempotencyKey,
        BigDecimal amount
) {
}
