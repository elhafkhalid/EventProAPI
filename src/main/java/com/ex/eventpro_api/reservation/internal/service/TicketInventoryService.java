package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;

public interface TicketInventoryService {
    TicketCategoriesPerEvent lockAndDecrementStock(Long ticketCategoriesPerEventId, Integer quatity);

    void releaseStock(Long ticketCategoriesPerEventId, Integer quantity);
}

