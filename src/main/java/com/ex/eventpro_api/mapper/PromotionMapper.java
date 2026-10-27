package com.ex.eventpro_api.mapper;

import com.ex.eventpro_api.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.dto.response.PromotionResponse;
import com.ex.eventpro_api.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    // 1. DTO -> Entité
    @Mapping(target = "id", ignore = true)          // Géré par la BDD
    @Mapping(target = "event", ignore = true)       // Sera cherché par le Service via eventId
    @Mapping(target = "usageCount", ignore = true)
    // Sera initialisé à 0 par le Service
    Promotion toEntity(PromotionCreateRequest request);

    // 2. Entité -> DTO
    // Pour la réponse, on veut extraire l'ID de l'événement lié
    @Mapping(target = "eventId", source = "event.id")
    PromotionResponse toResponse(Promotion promotion);
}