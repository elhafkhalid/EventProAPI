package com.ex.eventpro_api.service;

import com.ex.eventpro_api.entity.ReservationLine;

import java.math.BigDecimal;
import java.util.List;

public interface PricingService {
    BigDecimal calculateTotal(List<ReservationLine> lines, String promoCode);
}
