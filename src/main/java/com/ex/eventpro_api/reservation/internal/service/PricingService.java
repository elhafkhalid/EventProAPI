package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;

import java.math.BigDecimal;
import java.util.List;

public interface PricingService {
    BigDecimal calculateTotal(List<ReservationLine> lines, String promoCode);
}
