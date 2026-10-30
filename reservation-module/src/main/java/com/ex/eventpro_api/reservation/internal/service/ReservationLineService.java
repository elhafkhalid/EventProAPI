package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;

import java.util.List;

public interface ReservationLineService {
    // On utilise PromotionDto au lieu de Promotion !
    List<ReservationLine> processLines(ReservationRequest request, Reservation reservation, PromotionDto promo);
}