package com.ex.eventpro_api.event.api;

import com.ex.eventpro_api.event.api.dto.EventDto;
import com.ex.eventpro_api.event.api.dto.TicketStockDto;

public interface EventFacade {
    EventDto getEventById(Long eventId);

    // On renvoie le DTO au lieu d'un simple BigDecimal
    TicketStockDto reserveStock(Long ticketCategoriesPerEventId, Integer quantity);

    void releaseStock(Long ticketCategoriesPerEventId, Integer quantity);
}