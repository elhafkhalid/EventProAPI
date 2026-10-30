package com.ex.eventpro_api.promotion.internal.service.Impl;

import com.ex.eventpro_api.promotion.internal.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.promotion.internal.dto.response.PromotionResponse;
import com.ex.eventpro_api.promotion.internal.entity.Promotion;
import com.ex.eventpro_api.promotion.internal.mapper.PromotionMapper;
import com.ex.eventpro_api.promotion.internal.repository.PromotionRepository;
import com.ex.eventpro_api.promotion.internal.service.PromotionService;
import com.ex.eventpro_api.event.api.EventFacade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final EventFacade eventFacade;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionCreateRequest request) {

        // 1. ARCHITECTURE SENIOR : On valide l'événement via sa Façade
        // (Si l'événement n'existe pas, la Façade lèvera une exception)
        eventFacade.getEventById(request.eventId());

        // 2. Vérifier si l'événement a déjà une promotion (Règle métier)
        if (promotionRepository.findByEventId(request.eventId()).isPresent()) {
            throw new IllegalArgumentException("Cet événement possède déjà une promotion active.");
        }

        // 3. Vérifier si le code promo existe déjà
        if (promotionRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException("Le code promo '" + request.code() + "' existe déjà.");
        }

        // 4. MAPPER
        Promotion promotion = promotionMapper.toEntity(request);
        promotion.setUsageCount(0);

        // 6. Sauvegarde
        Promotion savedPromotion = promotionRepository.save(promotion);

        return promotionMapper.toResponse(savedPromotion);
    }
}
