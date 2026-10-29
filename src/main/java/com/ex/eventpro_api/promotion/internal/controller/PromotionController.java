package com.ex.eventpro_api.promotion.internal.controller;

import com.ex.eventpro_api.promotion.internal.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.promotion.internal.dto.response.PromotionResponse;
import com.ex.eventpro_api.promotion.internal.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    // Endpoint POST pour créer une promotion
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionResponse createPromotion(@RequestBody PromotionCreateRequest request) {
        return promotionService.createPromotion(request);
    }

}
