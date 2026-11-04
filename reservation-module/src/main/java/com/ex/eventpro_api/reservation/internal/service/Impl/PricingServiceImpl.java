package com.ex.eventpro_api.reservation.internal.service.Impl;

import com.ex.eventpro_api.promotion.api.PromotionFacade;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import com.ex.eventpro_api.reservation.internal.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    // On injecte la Façade !
    private final PromotionFacade promotionFacade;

    @Override
    public BigDecimal calculateTotal(List<ReservationLine> lines, String promotionCode) {
        // 1. Calcul du sous-total (interne au module Reservation)
        BigDecimal total = calculateSubTotal(lines);

        // 2. Application de la réduction (déléguée au module Promotion)
        if (promotionCode != null && !promotionCode.trim().isEmpty()) {
            // La Façade fait le calcul et renvoie le montant de la réduction
            BigDecimal discount = promotionFacade.calculateDiscount(promotionCode, total);
            total = total.subtract(discount);
        }

        // 3. Sécurité : le total ne peut pas être négatif
        return total.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateSubTotal(List<ReservationLine> lines) {
        return lines.stream()
                .map(ReservationLine::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}