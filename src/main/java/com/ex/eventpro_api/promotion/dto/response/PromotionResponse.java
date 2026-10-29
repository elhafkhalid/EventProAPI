package com.ex.eventpro_api.promotion.dto.response;

import com.ex.eventpro_api.promotion.enums.DiscountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PromotionResponse(
        Long id,
        Long eventId,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer usageLimit,
        Integer usageCount // Ici on le met, car le client a le droit de voir combien de fois elle a été utilisée
) {}