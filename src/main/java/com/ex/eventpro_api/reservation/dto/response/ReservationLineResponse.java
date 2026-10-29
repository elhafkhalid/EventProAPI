package com.ex.eventpro_api.reservation.dto.response;

import java.math.BigDecimal;

public record ReservationLineResponse(
        String categoryName,
        Integer quantity,
        BigDecimal fixedUnitPrice,
        BigDecimal subTotal
) {
}
