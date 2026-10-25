package com.ex.eventpro_api.mapper;

import com.ex.eventpro_api.dto.request.ReservationRequest;
import com.ex.eventpro_api.dto.response.ReservationLineResponse;
import com.ex.eventpro_api.dto.response.ReservationResponse;
import com.ex.eventpro_api.entity.Reservation;
import com.ex.eventpro_api.entity.ReservationLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    // 1. DTO Request -> Entité
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reference", ignore = true)       // Géré par le Service
    @Mapping(target = "client", ignore = true)          // Géré par le Service
    @Mapping(target = "creationDate", ignore = true)    // Géré par le Service
    @Mapping(target = "expirationDate", ignore = true)  // Géré par le Service
    @Mapping(target = "status", ignore = true)          // Géré par le Service
    @Mapping(target = "promotion", ignore = true)       // Géré par le Service
    @Mapping(target = "totalAmount", ignore = true)     // Géré par le Service
    @Mapping(target = "lines", ignore = true)           // Géré par le Service (à cause du verrou)
    Reservation toEntity(ReservationRequest request);

    // 2. Entité -> DTO Response (Le reçu global)
    // MapStruct va automatiquement comprendre qu'il doit utiliser la méthode toLineResponse pour la liste
    ReservationResponse toResponse(Reservation reservation);

    // 3. Méthode pour traduire une seule ligne
    // On doit dire à MapStruct où trouver le nom de la catégorie.
    // C'est : line -> category (TicketCategoriesPerEvent) -> category (TicketCategory) -> name
    @Mapping(target = "categoryName", source = "category.category.name")
    ReservationLineResponse toLineResponse(ReservationLine line);
}