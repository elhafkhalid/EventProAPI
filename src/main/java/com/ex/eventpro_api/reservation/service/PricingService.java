package com.ex.eventpro_api.reservation.service;

import com.ex.eventpro_api.reservation.entity.ReservationLine;

import java.math.BigDecimal;
import java.util.List;

public interface PricingService {
    BigDecimal calculateTotal(List<ReservationLine> lines, String promoCode);
}
