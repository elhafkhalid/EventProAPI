package com.ex.eventpro_api.event.internal.mapper;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.response.EventResponse;
import com.ex.eventpro_api.event.internal.dto.response.TicketCategoryResponse;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // 1. DTO Request -> Entité
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "createdAt", ignore = true) // Géré par Hibernate
    @Mapping(target = "updatedAt", ignore = true) // Géré par Hibernate
    @Mapping(target = "status", constant = "DRAFT") // CORRECTION : On crée toujours en DRAFT !
    Event toEntity(EventCreateRequest request);

    // 2. Entité -> DTO Response
    EventResponse toResponse(Event event);

    // 3. Mapper pour une seule catégorie (MapStruct va l'utiliser automatiquement pour les listes)
    // On dit à MapStruct : "Pour remplir le champ 'name', va chercher dans l'objet 'category' (TicketCategory)"
    @Mapping(target = "name", source = "category.name")
    TicketCategoryResponse toCategoryResponse(TicketCategoriesPerEvent entity);
}