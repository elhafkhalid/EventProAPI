package com.ex.eventpro_api.event.internal.facade;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.event.api.EventFacade;
import com.ex.eventpro_api.event.api.dto.EventDto;
import com.ex.eventpro_api.event.api.dto.TicketStockDto;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.internal.repository.EventRepository;
import com.ex.eventpro_api.event.internal.repository.TicketCategoriesPerEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; // Changé en @Service
import org.springframework.transaction.annotation.Transactional; // Ajouté

@Service // 1. Plus sémantique
@RequiredArgsConstructor
@Transactional // 2. Sécurité pour les écritures en BDD
public class EventFacadeImpl implements EventFacade {

    private final EventRepository eventRepository;
    private final TicketCategoriesPerEventRepository ticketCategoryRepository;

    @Override
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

    @Override
    public TicketStockDto reserveStock(Long id, Long eventId, Integer quantity) {
        // 1. LE VERROU PESSIMISTE
        TicketCategoriesPerEvent stock = ticketCategoryRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable pour l'ID : " + id));

        if (!stock.getEvent().getId().equals(eventId))
            throw new IllegalArgumentException("La catégorie de billet spécifiée n'appartient pas à cet événement.");

        // 2. Vérification de l'événement
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

        // 5. On renvoie le DTO attendu par le module Reservation
        return new TicketStockDto(stock.getUnitPrice(), stock.getMaxQuotaPerClient());
    }

    @Override
    public void releaseStock(Long id, Integer quantity) {
        TicketCategoriesPerEvent stock = ticketCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable pour l'ID : " + id)); // 3. Message propre

        stock.setTotalQuantity(stock.getTotalQuantity() + quantity);
        ticketCategoryRepository.save(stock);
    }
}