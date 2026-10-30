package com.ex.eventpro_api.reservation.internal.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReservationResponse(
        Long id,
        String reference,
        LocalDateTime creationDate,
        LocalDateTime expirationDate,
        String status,
        BigDecimal totalAmount,
        List<ReservationLineResponse> lines
) {
}
