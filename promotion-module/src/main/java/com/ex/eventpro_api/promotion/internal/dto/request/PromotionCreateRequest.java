package com.ex.eventpro_api.promotion.internal.dto.request;

import com.ex.eventpro_api.promotion.internal.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PromotionCreateRequest(
        Long eventId, // On ne demande que l'ID de l'événement
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer usageLimit
        // On ne met PAS usageCount ici (c'est le serveur qui gère)
) {}