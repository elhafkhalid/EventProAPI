package com.ex.eventpro_api.promotion.internal.service.Impl;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.promotion.api.PromotionFacade;
import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import com.ex.eventpro_api.promotion.internal.entity.Promotion;
import com.ex.eventpro_api.promotion.internal.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionFacadeImpl implements PromotionFacade {

    private final PromotionRepository promotionRepository;

    @Override
    public PromotionDto getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo introuvable : " + code));

        return PromotionDto.builder()
                .id(promotion.getId())
                .eventId(promotion.getEventId())
                .code(promotion.getCode())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .usageLimit(promotion.getUsageLimit())
                .usageCount(promotion.getUsageCount())
                .build();
    }

    @Override
    public void incrementUsageCount(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Code promo introuvable : " + code));

        promotion.setUsageCount(promotion.getUsageCount() + 1);
        promotionRepository.save(promotion);
    }
}