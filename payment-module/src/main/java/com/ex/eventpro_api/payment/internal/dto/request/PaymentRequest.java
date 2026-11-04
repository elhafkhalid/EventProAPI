package com.ex.eventpro_api.payment.internal.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(

        @NotNull(message = "L'identifiant du client est obligatoire")
        Long clientId,

        @NotNull(message = "L'identifiant de la réservation est obligatoire")
        Long reservationId,

        @NotBlank(message = "La clé d'idempotence est obligatoire")
        @Size(max = 100, message = "La clé d'idempotence ne doit pas dépasser 100 caractères")
        String idempotencyKey,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
        BigDecimal amount
) {}