package com.ex.eventpro_api.promotion.api.dto;

import lombok.Builder;

@Builder
public record PromotionDto(
        Long id,
        Long eventId,
        String code
) {}