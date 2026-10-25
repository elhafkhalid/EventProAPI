package com.ex.eventpro_api.dto.request;

import lombok.Builder;

@Builder
public record ReservationLineRequest(
        Long ticketCategoriesPerEventId,
        Integer quantity
) {
}
