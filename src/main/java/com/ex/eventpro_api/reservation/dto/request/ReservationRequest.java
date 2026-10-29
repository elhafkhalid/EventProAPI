package com.ex.eventpro_api.reservation.dto.request;

import java.util.List;


public record ReservationRequest(
        Long clientId,
        Long eventId,
        String promotionCode,
        List<ReservationLineRequest> lines
) {
}
