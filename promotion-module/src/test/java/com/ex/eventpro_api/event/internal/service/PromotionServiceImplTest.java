package com.ex.eventpro_api.event.internal.service;


import com.ex.eventpro_api.event.api.EventFacade;
import com.ex.eventpro_api.promotion.internal.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.promotion.internal.entity.Promotion;
import com.ex.eventpro_api.promotion.internal.mapper.PromotionMapper;
import com.ex.eventpro_api.promotion.internal.repository.PromotionRepository;
import com.ex.eventpro_api.promotion.internal.service.Impl.PromotionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionMapper promotionMapper;

    @Mock
    private EventFacade eventFacade;


    @InjectMocks
    PromotionServiceImpl promotionService;

    @Test
    void shouldThrowExceptionWhenEventHasAlreadyPromotion() {
        PromotionCreateRequest request = PromotionCreateRequest.builder()
                .eventId(1L)
                .code("promo20")
                .build();

        when(promotionRepository.findByEventId(1L)).thenReturn(Optional.of(new Promotion()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            promotionService.createPromotion(request);
        });

        assertTrue(exception.getMessage().contains("possède déjà une promotion"));
    }





}
