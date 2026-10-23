package com.ex.eventpro_api.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime salesOpeningDate,
        LocalDateTime salesClosingDate,
        String status,
        Integer capacity
) {
}
