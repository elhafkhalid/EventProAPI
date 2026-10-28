package com.ex.eventpro_api.scheduler;

import com.ex.eventpro_api.entity.Reservation;
import com.ex.eventpro_api.entity.ReservationLine;
import com.ex.eventpro_api.enums.ReservationStatus;
import com.ex.eventpro_api.repository.ReservationRepository;
import com.ex.eventpro_api.service.TicketInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final TicketInventoryService inventoryService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingReservation() {

        log.info("🤖 Scheduler : Recherche des réservations expirées...");

        LocalDateTime now = LocalDateTime.now();

        List<Reservation> expiredReservations = reservationRepository.findByStatusAndExpirationDateBefore(ReservationStatus.PENDING_PAYMENT, now);

        if (expiredReservations.isEmpty()) return;

        for (Reservation reservation : expiredReservations) {
            log.info("🤖 Expiration de la réservation : {}", reservation.getReference());

            for (ReservationLine line : reservation.getLines()) {
                inventoryService.releaseStock(
                        line.getCategory().getId(),
                        line.getQuantity());

            }

            reservation.setStatus(ReservationStatus.EXPIRED);
        }

        reservationRepository.saveAll(expiredReservations);
        log.info("🤖 Scheduler : {} réservation(s) expirée(s) traitée(s).", expiredReservations.size());

    }

}
