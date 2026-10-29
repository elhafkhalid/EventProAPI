package com.ex.eventpro_api.event.internal.dto.request;


import java.time.LocalDateTime;
import java.util.List;


public record EventCreateRequest(
        Long organizerId, // On passe l'ID car on n'a pas encore l'authentification
        String title,
        String description,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime salesOpeningDate,
        LocalDateTime salesClosingDate,
        Integer capacity,
        List<TicketCategoryRequest> categories // La liste des catégori
) {
}
