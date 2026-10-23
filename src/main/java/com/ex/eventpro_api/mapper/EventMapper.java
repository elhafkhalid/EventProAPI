package com.ex.eventpro_api.mapper;

import com.ex.eventpro_api.dto.request.EventCreateRequest;
import com.ex.eventpro_api.dto.response.EventResponse;
import com.ex.eventpro_api.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface EventMapper {

    // 1. Transformer le DTO de création en Entité
    @Mapping(target = "id", ignore = true)          // L'ID est géré par la BDD
    @Mapping(target = "organizer", ignore = true)   // Sera défini dans le Service
    @Mapping(target = "categories", ignore = true)  // Sera défini dans le Service (car il faut chercher les TicketCategory en BDD)
    @Mapping(target = "status", constant = "DRAFT") // Un nouvel événement commence toujours en DRAFT

    Event toEntity(EventCreateRequest request);

    // 2. Transformer l'Entité en DTO de réponse
    EventResponse toResponse(Event event);
}