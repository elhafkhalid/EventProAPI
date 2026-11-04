package com.ex.eventpro_api.reservation.internal.service.Impl;

import com.ex.eventpro_api.event.api.EventFacade;
import com.ex.eventpro_api.event.api.dto.TicketStockDto;
import com.ex.eventpro_api.reservation.internal.dto.request.ReservationLineRequest;
import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import com.ex.eventpro_api.reservation.internal.repository.ReservationLineRepository;
import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import com.ex.eventpro_api.reservation.internal.service.ReservationLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationLineServiceImpl implements ReservationLineService {

    private final EventFacade eventFacade;
    private final ReservationLineRepository reservationLineRepository;

    @Override
    public List<ReservationLine> processLines(ReservationRequest request, Reservation reservation, PromotionDto promo){
        // La magie du Stream : on transforme chaque Request en ReservationLine
        return request.lines().stream()
                .map(lineReq -> buildReservationLine(request, reservation, lineReq))
                .toList(); // .toList() renvoie une liste immuable (Java 16+)
    }

    // ==========================================
    // MÉTHODES PRIVÉES (Les petites briques)
    // ==========================================

    /**
     * Construit une ligne de réservation après avoir verrouillé le stock et vérifié le quota.
     */
    private ReservationLine buildReservationLine(ReservationRequest request, Reservation reservation, ReservationLineRequest lineReq) {

        // 1. On réserve le stock (Verrou) et on récupère le prix + maxQuota
        TicketStockDto stockInfo = eventFacade.reserveStock(
                lineReq.ticketCategoriesPerEventId(),
                request.eventId(),
                lineReq.quantity()
        );

        // 2. Vérification du quota
        checkClientQuota(
                request.clientId(),
                lineReq.ticketCategoriesPerEventId(),
                lineReq.quantity(),
                stockInfo.maxQuotaPerClient()
        );

        // 3. Création et retour de la ligne
        return ReservationLine.builder()
                .reservation(reservation)
                .ticketCategoriesPerEventId(lineReq.ticketCategoriesPerEventId())
                .quantity(lineReq.quantity())
                .fixedUnitPrice(stockInfo.unitPrice())
                .subTotal(stockInfo.unitPrice().multiply(BigDecimal.valueOf(lineReq.quantity())))
                .build();
    }

    private void checkClientQuota(Long clientId, Long tickePerEventId, Integer requestedQuantity, Integer maxQuota) {
        Integer alreadyBought = reservationLineRepository.countTotalQuantityBoughtByClientForCategory(clientId, tickePerEventId);

        if (alreadyBought + requestedQuantity > maxQuota) {
            throw new IllegalArgumentException(
                    "Limite d'achat dépassée. Maximum autorisé : " + maxQuota +
                            ". Vous en avez déjà réservé : " + alreadyBought
            );
        }
    }
}