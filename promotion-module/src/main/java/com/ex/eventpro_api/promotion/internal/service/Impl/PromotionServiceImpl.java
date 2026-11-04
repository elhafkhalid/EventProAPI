package com.ex.eventpro_api.promotion.internal.service.Impl;

import com.ex.eventpro_api.event.api.dto.EventDto;
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
    public PromotionResponse createPromotion(PromotionCreateRequest request) {

        // 1. Valider l'événement
        EventDto event = eventFacade.getEventById(request.eventId());

        // 2. Vérifications d'unicité
        checkEventHasNoPromotion(request.eventId());
        checkCodeIsUnique(request.code());

        // 3. MAPPER
        Promotion promotion = promotionMapper.toEntity(request);

        // 4. VALIDATIONS MÉTIER (Les grands oublis corrigés)
        promotion.validateDates();          // Vérifie que startDate < endDate
        promotion.validateDiscountValue();  // Vérifie que le % <= 100

        // 5. Sauvegarde (Le usageCount est déjà à 0 grâce au @Builder.Default)
        Promotion savedPromotion = promotionRepository.save(promotion);

        return promotionMapper.toResponse(savedPromotion);
    }

    // ==========================================
    // MÉTHODES PRIVÉES (Pour un service ultra clean)
    // ==========================================

    private void checkEventHasNoPromotion(Long eventId) {
        if (promotionRepository.findByEventId(eventId).isPresent()) {
            throw new IllegalArgumentException("Cet événement possède déjà une promotion active.");
        }
    }

    private void checkCodeIsUnique(String code) {
        if (promotionRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Le code promo '" + code + "' existe déjà.");
        }
    }
}