package com.ex.eventpro_api.event.internal.service.Impl;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.event.api.EventFacade;
import com.ex.eventpro_api.event.api.dto.EventDto;
import com.ex.eventpro_api.event.api.dto.TicketStockDto;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.internal.repository.EventRepository;
import com.ex.eventpro_api.event.internal.repository.TicketCategoriesPerEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EventFacadeImpl implements EventFacade {

    private final EventRepository eventRepository;
    private final TicketCategoriesPerEventRepository ticketCategoryRepository; // Injecte-le


    public EventDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'ID : " + eventId));

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .status(event.getStatus().name())
                .salesOpeningDate(event.getSalesOpeningDate())
                .salesClosingDate(event.getSalesClosingDate())
                .build();
    }



    public TicketStockDto reserveStock(Long id, Integer quantity) {

        // 1. LE VERROU PESSIMISTE
        var stock = ticketCategoryRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable pour l'ID : " + id));

        // On vérifie si l'événement est publié et si on est dans les dates de vente
        if (!stock.getEvent().isOpenForSales()) {
            throw new IllegalArgumentException("Les ventes pour l'événement '" + stock.getEvent().getTitle() + "' ne sont pas ouvertes ou sont terminées.");
        }

        // 3. Vérification du stock
        if (stock.getTotalQuantity() < quantity) {
            throw new IllegalArgumentException("Stock insuffisant pour cette catégorie.");
        }

        // 4. Décrémentation
        stock.setTotalQuantity(stock.getTotalQuantity() - quantity);
        ticketCategoryRepository.save(stock);

        // 5. On renvoie les infos
        return new TicketStockDto(stock.getUnitPrice(), stock.getMaxQuotaPerClient());
    }


    public void releaseStock(Long id, Integer quantity) {
        TicketCategoriesPerEvent stock = ticketCategoryRepository.findById(id).orElseThrow();
        stock.setTotalQuantity(stock.getTotalQuantity() + quantity);
        ticketCategoryRepository.save(stock);
    }


}