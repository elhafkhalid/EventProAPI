package com.ex.eventpro_api.reservation.internal.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TicketResponse(
        Long id,
        String code,
        String status,
        LocalDateTime issueDate,
        Long ticketCategoriesPerEventId // Pour savoir si c'est du VIP ou du Standard
) {}