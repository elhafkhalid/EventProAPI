package com.ex.eventpro_api.event.internal.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TicketCategoryRequest(

        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
        String name,

        @NotNull(message = "Le prix unitaire est obligatoire")
        @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
        BigDecimal unitPrice,

        @NotNull(message = "La quantité totale est obligatoire")
        @Min(value = 1, message = "La quantité totale doit être d'au moins 1")
        Integer totalQuantity,

        @NotNull(message = "Le quota par client est obligatoire")
        @Min(value = 1, message = "Le quota par client doit être d'au moins 1")
        Integer maxQuotaPerClient
) {
}