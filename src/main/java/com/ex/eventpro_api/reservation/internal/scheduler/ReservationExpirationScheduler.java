package com.ex.eventpro_api.reservation.internal.scheduler;

import com.ex.eventpro_api.event.api.EventFacade;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import com.ex.eventpro_api.reservation.internal.enums.ReservationStatus;
import com.ex.eventpro_api.reservation.internal.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final ReservationRepository reservationRepository;
    private final EventFacade eventFacade; // On utilise la Façade !

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingReservations() {
        log.info("🤖 Scheduler : Recherche des réservations expirées...");
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> expiredReservations = reservationRepository
                .findByStatusAndExpirationDateBefore(ReservationStatus.PENDING_PAYMENT, now);

        if (expiredReservations.isEmpty()) {
            return;
        }

        for (Reservation reservation : expiredReservations) {
            log.info("🤖 Expiration de la réservation : {}", reservation.getReference());

            for (ReservationLine line : reservation.getLines()) {
                // On rend le stock via l'ID et la Façade
                eventFacade.releaseStock(
                        line.getTicketCategoriesPerEventId(),
                        line.getQuantity()
                );
            }

            reservation.setStatus(ReservationStatus.EXPIRED);
        }

        reservationRepository.saveAll(expiredReservations);
        log.info("🤖 Scheduler : {} réservation(s) expirée(s) traitée(s).", expiredReservations.size());
    }
}