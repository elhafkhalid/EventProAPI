package com.ex.eventpro_api.reservation.internal.mapper;

import com.ex.eventpro_api.reservation.internal.dto.response.TicketResponse;
import com.ex.eventpro_api.reservation.internal.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    // On extrait l'ID de la catégorie depuis la ligne de réservation
    @Mapping(target = "ticketCategoriesPerEventId", source = "reservationLine.ticketCategoriesPerEventId")
    TicketResponse toResponse(Ticket ticket);
}