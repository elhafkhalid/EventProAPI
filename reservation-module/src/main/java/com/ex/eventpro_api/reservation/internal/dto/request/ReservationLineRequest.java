package com.ex.eventpro_api.reservation.internal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationLineRequest(

        @NotNull(message = "L'identifiant de la catégorie de billets est obligatoire")
        Long ticketCategoriesPerEventId,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "Vous devez réserver au moins 1 billet")
        Integer quantity
) {}