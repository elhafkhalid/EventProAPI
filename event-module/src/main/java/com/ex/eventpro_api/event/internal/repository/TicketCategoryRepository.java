package com.ex.eventpro_api.event.internal.repository;

import com.ex.eventpro_api.event.internal.entity.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
    Optional<TicketCategory> findByNameIgnoreCase(String name);
}