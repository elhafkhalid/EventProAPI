package com.ex.eventpro_api.promotion.api;

import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import java.math.BigDecimal;

public interface PromotionFacade {

    // Récupérer les infos de base (ID, eventId, code)
    PromotionDto getPromotionByCode(String code);

    // Valider la promo (existence, appartenance à l'événement, dates, quota)
    void validatePromotion(String code, Long eventId);

    // Calculer la réduction (le module Promotion fait le calcul lui-même)
    BigDecimal calculateDiscount(String code, BigDecimal amount);

    // Incrémenter le compteur d'utilisation
    void incrementUsage(String code);
}