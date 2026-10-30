package com.ex.eventpro_api.reservation.internal.dto.request;


public record ReservationLineRequest(
        Long ticketCategoriesPerEventId,
        Integer quantity
) {
}
