package com.ex.eventpro_api.promotion.api;

import com.ex.eventpro_api.promotion.api.dto.PromotionDto;

public interface PromotionFacade {
    // Récupérer une promo par son code (pour appliquer la réduction)
    PromotionDto getPromotionByCode(String code);
    void incrementUsageCount(String code);
}