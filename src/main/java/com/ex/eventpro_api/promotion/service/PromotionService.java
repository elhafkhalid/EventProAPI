package com.ex.eventpro_api.promotion.service;

import com.ex.eventpro_api.promotion.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.promotion.dto.response.PromotionResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);
}
