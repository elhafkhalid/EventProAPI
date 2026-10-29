package com.ex.eventpro_api.event.repository;

import com.ex.eventpro_api.event.entity.TicketCategoriesPerEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketCategoriesPerEventRepository extends JpaRepository<TicketCategoriesPerEvent, Long> {

    // LA MÉTHODE MAGIQUE POUR ÉVITER LA SURVENTE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketCategoriesPerEvent t WHERE t.id = :id")
    Optional<TicketCategoriesPerEvent> findByIdWithLock(@Param("id") Long id);

}
