package com.ex.eventpro_api.service;

import com.ex.eventpro_api.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.dto.response.PromotionResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionCreateRequest request);
}
