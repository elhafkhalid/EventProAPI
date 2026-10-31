package com.ex.eventpro_api.event.internal.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record TicketCategoryResponse(
        Long id,
        String name,
        BigDecimal unitPrice,
        Integer totalQuantity,
        Integer maxQuotaPerClient
) {}