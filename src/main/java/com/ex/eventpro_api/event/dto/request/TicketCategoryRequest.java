package com.ex.eventpro_api.event.dto.request;

import java.math.BigDecimal;


public record TicketCategoryRequest(
        String name,
        BigDecimal unitPrice,
        Integer totalQuantity,
        Integer maxQuotaPerClient
) {

}



