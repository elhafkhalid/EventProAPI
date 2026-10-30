package com.ex.eventpro_api.promotion.internal.service;

import com.ex.eventpro_api.promotion.internal.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.promotion.internal.dto.response.PromotionResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);
}
