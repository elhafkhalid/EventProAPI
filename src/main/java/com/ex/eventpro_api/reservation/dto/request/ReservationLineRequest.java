package com.ex.eventpro_api.reservation.dto.request;


public record ReservationLineRequest(
        Long ticketCategoriesPerEventId,
        Integer quantity
) {
}
