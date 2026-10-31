package com.ex.eventpro_api.event.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record EventCreateRequest(

        @NotNull(message = "L'identifiant de l'organisateur est obligatoire")
        Long organizerId,

        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
        String title,

        @Size(max = 5000, message = "La description ne doit pas dépasser 5000 caractères")
        String description,

        @NotBlank(message = "Le lieu est obligatoire")
        @Size(max = 255, message = "Le lieu ne doit pas dépasser 255 caractères")
        String location,

        @NotNull(message = "La date de début est obligatoire")
        @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
        LocalDateTime startDate,

        @NotNull(message = "La date de fin est obligatoire")
        @Future(message = "La date de fin doit être dans le futur")
        LocalDateTime endDate,

        @NotNull(message = "L'ouverture des ventes est obligatoire")
        @FutureOrPresent(message = "L'ouverture des ventes ne peut pas être dans le passé")
        LocalDateTime salesOpeningDate,

        @NotNull(message = "La fermeture des ventes est obligatoire")
        @Future(message = "La fermeture des ventes doit être dans le futur")
        LocalDateTime salesClosingDate,

        @NotNull(message = "La capacité est obligatoire")
        @Min(value = 1, message = "La capacité doit être d'au moins 1 place")
        Integer capacity,

        // --- LA MAGIE POUR LES LISTES ---
        @NotEmpty(message = "Un événement doit avoir au moins une catégorie de billets")
        @Valid // <-- TRÈS IMPORTANT : Force la validation à l'intérieur de chaque TicketCategoryRequest
        List<TicketCategoryRequest> categories
) {
}