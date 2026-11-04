package com.ex.eventpro_api.payment.internal.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
        Long id,
        String reference,
        Long reservationId,
        BigDecimal amount,       // <-- LE CHAMP CRUCIAL AJOUTÉ
        LocalDateTime paymentDate,
        String status
) {}