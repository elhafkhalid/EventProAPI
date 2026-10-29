package com.ex.eventpro_api.event.api.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record EventDto(
        Long id,
        String title,
        String status,
        LocalDateTime salesOpeningDate,
        LocalDateTime salesClosingDate
) {}