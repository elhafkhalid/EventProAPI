package com.ex.eventpro_api.promotion.internal.mapper;

import com.ex.eventpro_api.promotion.internal.dto.request.PromotionCreateRequest;
import com.ex.eventpro_api.promotion.internal.dto.response.PromotionResponse;
import com.ex.eventpro_api.promotion.internal.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PromotionMapper {

    // 1. DTO -> Entité
    @Mapping(target = "id", ignore = true)          // Géré par la BDD
    @Mapping(target = "usageCount", ignore = true)  // Géré par l'entité (valeur par défaut 0)
    @Mapping(target = "createdAt", ignore = true)   // Géré par Hibernate
    @Mapping(target = "updatedAt", ignore = true)   // Géré par Hibernate
    Promotion toEntity(PromotionCreateRequest request);

    // 2. Entité -> DTO Response
    PromotionResponse toResponse(Promotion promotion);
}