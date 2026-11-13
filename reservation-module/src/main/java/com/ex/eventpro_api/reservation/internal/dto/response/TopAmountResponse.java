package com.ex.eventpro_api.reservation.internal.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TopAmountResponse(
        Long id,
        BigDecimal totalAmount
) {
}
