package com.ex.eventpro_api.service.impl;

import com.ex.eventpro_api.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.dto.response.PromotionResponse;
import com.ex.eventpro_api.entity.Event;
import com.ex.eventpro_api.entity.Promotion;
import com.ex.eventpro_api.exception.ResourceNotFoundException;
import com.ex.eventpro_api.mapper.PromotionMapper;
import com.ex.eventpro_api.repository.EventRepository;
import com.ex.eventpro_api.repository.PromotionRepository;
import com.ex.eventpro_api.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final EventRepository eventRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionResponse createPromotion(PromotionCreateRequest request) {

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("event intr avec id : " + request.eventId()));

        if (promotionRepository.findByEventId(request.eventId()).isPresent()) {
            throw new IllegalArgumentException(" cet event possede deja une promotion active ");
        }

        if(promotionRepository.findByCode(request.code()).isPresent()){
            throw new IllegalArgumentException(" le code promo " + request.code() + " exist deja ");
        }

        Promotion promotion = promotionMapper.toEntity(request);

        promotion.setEvent(event);
        promotion.setUsageCount(0);

        Promotion savedPromotion = promotionRepository.save(promotion);
        return promotionMapper.toResponse(savedPromotion);
    }
}
