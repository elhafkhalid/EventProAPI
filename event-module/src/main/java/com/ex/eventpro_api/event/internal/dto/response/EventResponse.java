package com.ex.eventpro_api.event.internal.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventResponse(
        Long id,
        Long organizerId, // <-- Ajouté pour le Frontend
        String title,
        String description,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime salesOpeningDate,
        LocalDateTime salesClosingDate,
        String status,
        Integer capacity,
        LocalDateTime createdAt, // <-- Ajouté (Audit)
        List<TicketCategoryResponse> categories // <-- Ajouté (Le plus important !)
) {}