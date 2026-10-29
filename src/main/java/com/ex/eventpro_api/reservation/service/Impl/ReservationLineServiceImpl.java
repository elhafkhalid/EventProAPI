package com.ex.eventpro_api.reservation.service.Impl;

import com.ex.eventpro_api.reservation.dto.request.ReservationLineRequest;
import com.ex.eventpro_api.reservation.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.entity.Reservation;
import com.ex.eventpro_api.reservation.entity.ReservationLine;
import com.ex.eventpro_api.event.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.entity.Event;
import com.ex.eventpro_api.promotion.entity.Promotion;
import com.ex.eventpro_api.reservation.repository.ReservationLineRepository;
import com.ex.eventpro_api.reservation.service.ReservationLineService;
import com.ex.eventpro_api.reservation.service.TicketInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationLineServiceImpl implements ReservationLineService {

    private final TicketInventoryService inventoryService;
    private final ReservationLineRepository reservationLineRepository;

    @Override
    public List<ReservationLine> processLines(ReservationRequest request, Reservation reservation, Promotion promo) {
        List<ReservationLine> lines = new ArrayList<>();

        for (ReservationLineRequest lineReq : request.lines()) {
            ReservationLine line = createReservationLine(reservation, lineReq, request.clientId(), promo);
            lines.add(line);
        }

        return lines;
    }

    private ReservationLine createReservationLine(Reservation reservation, ReservationLineRequest lineReq, Long clientId, Promotion promo) {
        // 1. Verrouiller le stock
        TicketCategoriesPerEvent stock = inventoryService.lockAndDecrementStock(
                lineReq.ticketCategoriesPerEventId(),
                lineReq.quantity()
        );

        // 2. VÉRIFICATION : La promotion appartient-elle à cet événement ?
        if (promo != null && !promo.getEvent().getId().equals(stock.getEvent().getId())) {
            throw new IllegalArgumentException("Le code promo '" + promo.getCode() + "' n'est pas valide pour cet événement.");
        }

        // 3. Vérifier le quota max par client
        checkClientQuota(clientId, stock, lineReq.quantity());

        // 4. Vérifier si l'événement est ouvert aux ventes
        checkEventSalesStatus(stock.getEvent());

        // 5. Construire la ligne
        return ReservationLine.builder()
                .reservation(reservation)
                .category(stock)
                .quantity(lineReq.quantity())
                .fixedUnitPrice(stock.getUnitPrice())
                .subTotal(stock.getUnitPrice().multiply(BigDecimal.valueOf(lineReq.quantity())))
                .build();
    }

    private void checkClientQuota(Long clientId, TicketCategoriesPerEvent stock, Integer requestedQuantity) {
        Integer alreadyBought = reservationLineRepository.countTotalQuantityBoughtByClientForCategory(
                clientId, stock.getId()
        );

        if (alreadyBought + requestedQuantity > stock.getMaxQuotaPerClient()) {
            throw new IllegalArgumentException(
                    "Limite d'achat dépassée pour la catégorie " + stock.getCategory().getName() +
                            ". Maximum autorisé : " + stock.getMaxQuotaPerClient() +
                            ". Vous en avez déjà réservé : " + alreadyBought
            );
        }
    }

    private void checkEventSalesStatus(Event event) {
        if (!event.isOpenForSales()) {
            throw new IllegalArgumentException("Les ventes pour l'événement '" + event.getTitle() + "' ne sont pas ouvertes ou sont terminées.");
        }
    }
}