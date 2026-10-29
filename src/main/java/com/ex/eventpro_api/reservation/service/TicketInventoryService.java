package com.ex.eventpro_api.reservation.service;

import com.ex.eventpro_api.event.entity.TicketCategoriesPerEvent;

public interface TicketInventoryService {
    TicketCategoriesPerEvent lockAndDecrementStock(Long ticketCategoriesPerEventId, Integer quatity);

    void releaseStock(Long ticketCategoriesPerEventId, Integer quantity);
}

