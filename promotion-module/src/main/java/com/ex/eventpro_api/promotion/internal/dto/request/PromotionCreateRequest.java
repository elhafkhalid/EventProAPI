package com.ex.eventpro_api.promotion.internal.dto.request;

import com.ex.eventpro_api.promotion.internal.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PromotionCreateRequest(

        @NotNull(message = "L'identifiant de l'événement est obligatoire")
        Long eventId,

        @NotBlank(message = "Le code promo est obligatoire")
        @Size(max = 50, message = "Le code promo ne doit pas dépasser 50 caractères")
        String code,

        @NotNull(message = "Le type de réduction est obligatoire")
        DiscountType discountType,

        @NotNull(message = "La valeur de la réduction est obligatoire")
        @DecimalMin(value = "0.01", message = "La réduction doit être supérieure à 0")
        BigDecimal discountValue,

        @NotNull(message = "La date de début est obligatoire")
        //@FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
        LocalDateTime startDate,

        @NotNull(message = "La date de fin est obligatoire")
        @Future(message = "La date de fin doit être dans le futur")
        LocalDateTime endDate,

        @NotNull(message = "La limite d'utilisation est obligatoire")
        @Min(value = 1, message = "La limite d'utilisation doit être d'au moins 1")
        Integer usageLimit

        // On ne met PAS usageCount ici (c'est le serveur qui gère)
) {}