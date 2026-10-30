package com.ex.eventpro_api.promotion.api.dto;

import com.ex.eventpro_api.promotion.internal.enums.DiscountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PromotionDto(
        Long id,
        Long eventId, // Très important pour vérifier que la promo correspond à l'événement
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer usageLimit,
        Integer usageCount
) {}