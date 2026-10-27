package com.ex.eventpro_api.service;

import com.ex.eventpro_api.entity.TicketCategoriesPerEvent;

public interface TicketInventoryService {
    TicketCategoriesPerEvent lockAndDecrementStock(Long TicketCategoriesPerEventId,Integer quatity);
}

