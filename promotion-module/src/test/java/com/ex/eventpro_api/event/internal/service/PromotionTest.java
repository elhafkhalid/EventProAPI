package com.ex.eventpro_api.event.internal.service;

import com.ex.eventpro_api.promotion.internal.entity.Promotion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromotionTest {

    @Test
    void shouldBeValidWhenDatesAreCorrectAndUsageCountIsLow() {

        Promotion promo = Promotion.builder()
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .usageLimit(100)
                .usageCount(10)
                .build();

        assertTrue(promo.isValid());

    }

    @Test
    void shouldBeInvalidWhenExpired() {

        Promotion promo = Promotion.builder()
                .startDate(LocalDateTime.now().minusDays(10))
                .endDate(LocalDateTime.now().minusDays(1))
                .usageCount(100)
                .usageLimit(10)
                .build();


        assertFalse(promo.isValid());
    }

    @Test
    void shouldBeInvalidWhenUsageLimitReached() {

        Promotion promo = Promotion.builder()
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .usageLimit(10)
                .usageCount(10)
                .build();

        assertFalse(promo.isValid());

    }

}
