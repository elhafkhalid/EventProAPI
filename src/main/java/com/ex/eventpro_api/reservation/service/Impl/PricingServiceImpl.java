package com.ex.eventpro_api.reservation.service.Impl;

import com.ex.eventpro_api.promotion.entity.Promotion;
import com.ex.eventpro_api.reservation.entity.ReservationLine;
import com.ex.eventpro_api.promotion.repository.PromotionRepository;
import com.ex.eventpro_api.reservation.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PromotionRepository promotionRepository;

    @Override
    public BigDecimal calculateTotal(List<ReservationLine> lines, String promoCode) {

        BigDecimal total = lines.stream()
                .map(ReservationLine::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (promoCode != null && !promoCode.trim().isEmpty()) {
            Promotion promo = promotionRepository.findByCode(promoCode)
                    .orElseThrow(() -> new IllegalArgumentException(" code promo invalide "));

            if (!promo.isValid())
                throw new IllegalArgumentException("Ce code promo est expiré ou a atteint sa limite d'utilisation");

            BigDecimal discount = promo.calculateDiscount(total);

            total = total.subtract(discount);

            promo.setUsageCount(promo.getUsageCount() + 1);
            promotionRepository.save(promo);
        }

        return total.max(BigDecimal.ZERO);

    }
}
