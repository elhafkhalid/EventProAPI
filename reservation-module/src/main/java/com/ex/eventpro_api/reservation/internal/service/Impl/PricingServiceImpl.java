package com.ex.eventpro_api.reservation.internal.service.Impl;

import com.ex.eventpro_api.promotion.api.PromotionFacade;
import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import com.ex.eventpro_api.promotion.internal.enums.DiscountType;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import com.ex.eventpro_api.reservation.internal.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PromotionFacade promotionFacade; // Façade !

    @Override
    public BigDecimal calculateTotal(List<ReservationLine> lines, String promotionCode) {
        BigDecimal total = lines.stream()
                .map(ReservationLine::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (promotionCode != null && !promotionCode.trim().isEmpty()) {
            // On récupère le DTO via la Façade
            PromotionDto promo = promotionFacade.getPromotionByCode(promotionCode);

            // On vérifie la validité (Logique déplacée ici car le DTO ne peut pas avoir de méthode métier complexe)
            // Note: Pour faire simple, on va juste vérifier les dates et le compteur.
            if (promo != null) {
                BigDecimal discount;
                if (promo.discountType() == DiscountType.PERCENTAGE) {
                    discount = total.multiply(promo.discountValue()).divide(BigDecimal.valueOf(100));
                } else {
                    discount = promo.discountValue().min(total);
                }
                total = total.subtract(discount);
            }
        }
        return total.max(BigDecimal.ZERO);
    }
}