package com.ex.eventpro_api.payment.internal.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
        Long id,
        String reference,
        Long reservationId,
        LocalDateTime paymentDate,
        String status
) {
}
