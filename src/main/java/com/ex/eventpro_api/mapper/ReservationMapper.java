package com.ex.eventpro_api.mapper;

import com.ex.eventpro_api.dto.response.ReservationLineResponse;
import com.ex.eventpro_api.dto.response.ReservationResponse;
import com.ex.eventpro_api.entity.Reservation;
import com.ex.eventpro_api.entity.ReservationLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    // 1. Entité -> DTO Response (Le reçu global)
    ReservationResponse toResponse(Reservation reservation);

    // 2. Méthode pour traduire une seule ligne (OBLIGATOIRE pour le categoryName)
    @Mapping(target = "categoryName", source = "category.category.name")
    ReservationLineResponse toLineResponse(ReservationLine line);

}