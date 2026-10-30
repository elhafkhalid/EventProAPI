package com.ex.eventpro_api.reservation.internal.dto.response;

import java.math.BigDecimal;

public record ReservationLineResponse(
        Long ticketCategoriesPerEventId,
        Integer quantity,
        BigDecimal fixedUnitPrice,
        BigDecimal subTotal
) {
}
