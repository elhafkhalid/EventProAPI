package com.ex.eventpro_api.reservation.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReservationRequest(

        @NotNull(message = "L'identifiant du client est obligatoire")
        Long clientId,

        @NotNull(message = "L'identifiant de l'événement est obligatoire")
        Long eventId,

        // Le code promo est optionnel (pas de @NotBlank), mais on limite sa taille au cas où
        @Size(max = 50, message = "Le code promo ne doit pas dépasser 50 caractères")
        String promotionCode,

        // La liste ne doit pas être vide, et on doit valider chaque élément à l'intérieur
        @NotEmpty(message = "Une réservation doit contenir au moins un billet")
        @Valid
        List<ReservationLineRequest> lines
) {}