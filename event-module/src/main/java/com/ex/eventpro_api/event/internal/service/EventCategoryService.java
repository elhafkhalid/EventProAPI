package com.ex.eventpro_api.event.internal.service;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;

import java.util.List;

public interface EventCategoryService {
    // Prépare, valide et renvoie la liste des catégories prêtes à être liées à l'événement
    List<TicketCategoriesPerEvent> buildAndValidateCategories(EventCreateRequest request, Event event);
}