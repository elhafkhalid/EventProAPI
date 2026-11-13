package com.ex.eventpro_api.reservation.api;

import java.math.BigDecimal;

public interface TopAmountClient {
    Long getClientId();
    BigDecimal getTotalAmount();
}
