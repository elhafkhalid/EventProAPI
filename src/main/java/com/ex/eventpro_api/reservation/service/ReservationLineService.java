package com.ex.eventpro_api.reservation.service;

import com.ex.eventpro_api.reservation.dto.request.ReservationLineRequest;
import com.ex.eventpro_api.reservation.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.entity.Reservation;
import com.ex.eventpro_api.reservation.entity.ReservationLine;
import com.ex.eventpro_api.promotion.entity.Promotion;

import java.util.List;

public interface ReservationLineService {
    // Prépare toutes les lignes du panier
    List<ReservationLine> processLines(ReservationRequest request, Reservation reservation, Promotion promo);
}