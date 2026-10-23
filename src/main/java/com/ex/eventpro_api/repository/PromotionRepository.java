package com.ex.eventpro_api.repository;

import com.ex.eventpro_api.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository {
    Optional<Promotion> findByCode(String code);
    Optional<Promotion> findByEventId(Long id);
}
