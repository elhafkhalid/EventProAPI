package com.ex.eventpro_api.promotion.repository;

import com.ex.eventpro_api.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);
    Optional<Promotion> findByEventId(Long id);
}
