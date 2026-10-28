package com.ex.eventpro_api.dto.request;

import lombok.Builder;

import java.util.List;


public record ReservationRequest(
        Long clientId,
        String promotionCode,
        List<ReservationLineRequest> lines
) {
}
