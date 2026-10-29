package com.ex.eventpro_api.reservation.api.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ReservationDto(
        Long id,
        Long clientId,
        BigDecimal totalAmount,
        String status,
        LocalDateTime expirationDate
) {}