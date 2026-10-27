package com.ex.eventpro_api.controller;

import com.ex.eventpro_api.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.dto.response.PromotionResponse;
import com.ex.eventpro_api.service.PromotionService;
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
