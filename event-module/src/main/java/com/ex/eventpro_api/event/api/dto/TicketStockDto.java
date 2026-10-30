package com.ex.eventpro_api.event.api.dto;

import java.math.BigDecimal;

public record TicketStockDto(
        BigDecimal unitPrice,
        Integer maxQuotaPerClient
) {}