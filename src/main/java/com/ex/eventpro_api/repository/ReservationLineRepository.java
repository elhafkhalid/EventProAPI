package com.ex.eventpro_api.repository;

import com.ex.eventpro_api.entity.ReservationLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationLineRepository extends JpaRepository<ReservationLine, Long> {
}
