package com.ex.eventpro_api.reservation.internal.dto.response;

import lombok.Builder;

@Builder
public record TopTicketsClientResponse(
        Long id,
        int totalTicket
) {
}
