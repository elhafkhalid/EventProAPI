package com.ex.eventpro_api.promotion.internal.facade;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.promotion.api.PromotionFacade;
import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import com.ex.eventpro_api.promotion.internal.entity.Promotion;
import com.ex.eventpro_api.promotion.internal.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionFacadeImpl implements PromotionFacade {

    private final PromotionRepository promotionRepository;

    @Override
    public PromotionDto getPromotionByCode(String code) {
        Promotion promo = getPromotionEntity(code);

        return PromotionDto.builder()
                .id(promo.getId())
                .eventId(promo.getEventId())
                .code(promo.getCode())
                .build();
    }

    @Override
    public void validatePromotion(String code, Long eventId) {
        Promotion promo = getPromotionEntity(code);

        // 1. Vérifier l'événement
        if (!promo.getEventId().equals(eventId)) {
            throw new IllegalArgumentException("Le code promo n'est pas valide pour cet événement.");
        }
        // 2. Vérifier les dates et le quota (méthode de l'entité)
        if (!promo.isValid()) {
            throw new IllegalArgumentException("Ce code promo est expiré ou a atteint sa limite.");
        }
    }

    @Override
    public BigDecimal calculateDiscount(String code, BigDecimal amount) {
        Promotion promo = getPromotionEntity(code);
        return promo.calculateDiscount(amount); // On utilise la méthode de l'entité !
    }

    @Override
    public void incrementUsage(String code) {
        Promotion promo = getPromotionEntity(code);
        promo.incrementUsage(); // Méthode de l'entité qui vérifie le quota
        promotionRepository.save(promo);
    }

    // Méthode privée pour éviter la répétition
    private Promotion getPromotionEntity(String code) {
        return promotionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo introuvable : " + code));
    }
}