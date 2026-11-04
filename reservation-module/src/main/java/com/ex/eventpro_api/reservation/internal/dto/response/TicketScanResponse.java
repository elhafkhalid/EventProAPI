package com.ex.eventpro_api.reservation.internal.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TicketScanResponse(
        String ticketCode,
        String status,
        LocalDateTime scanDate,
        Long reservationId // Pour savoir à quelle résa il appartient
) {}