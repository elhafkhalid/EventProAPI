package com.ex.eventpro_api.reservation.internal.mapper;

import com.ex.eventpro_api.reservation.internal.dto.response.ReservationLineResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.ReservationResponse;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationResponse toResponse(Reservation reservation);

    // SUPPRIME le @Mapping, il n'y en a plus besoin !
    ReservationLineResponse toLineResponse(ReservationLine line);
}