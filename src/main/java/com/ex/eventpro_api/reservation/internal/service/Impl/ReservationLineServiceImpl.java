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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationLineServiceImpl implements ReservationLineService {

    private final EventFacade eventFacade;
    private final ReservationLineRepository reservationLineRepository;

    @Override
    public List<ReservationLine> processLines(ReservationRequest request, Reservation reservation, PromotionDto promo) {
        List<ReservationLine> lines = new ArrayList<>();

        for (ReservationLineRequest lineReq : request.lines()) {
            // 1. On réserve le stock (Verrou) et on récupère le prix + maxQuota
            TicketStockDto stockInfo = eventFacade.reserveStock(lineReq.ticketCategoriesPerEventId(), lineReq.quantity());

            // 2. Vérification du quota
            checkClientQuota(request.clientId(), lineReq.ticketCategoriesPerEventId(), lineReq.quantity(), stockInfo.maxQuotaPerClient());

            // 3. Création de la ligne
            ReservationLine line = ReservationLine.builder()
                    .reservation(reservation)
                    .ticketCategoriesPerEventId(lineReq.ticketCategoriesPerEventId())
                    .quantity(lineReq.quantity())
                    .fixedUnitPrice(stockInfo.unitPrice()) // On utilise le prix du DTO
                    .subTotal(stockInfo.unitPrice().multiply(BigDecimal.valueOf(lineReq.quantity())))
                    .build();

            lines.add(line);
        }
        return lines;
    }

    private void checkClientQuota(Long clientId, Long ticketId, Integer requestedQuantity, Integer maxQuota) {
        // 1. On demande au repository combien le client en a déjà acheté
        Integer alreadyBought = reservationLineRepository.countTotalQuantityBoughtByClientForCategory(clientId, ticketId);

        // 2. On compare avec le maxQuota (qu'on a récupéré via la Façade !)
        if (alreadyBought + requestedQuantity > maxQuota) {
            throw new IllegalArgumentException(
                    "Limite d'achat dépassée. Maximum autorisé : " + maxQuota +
                            ". Vous en avez déjà réservé : " + alreadyBought
            );
        }
    }
}