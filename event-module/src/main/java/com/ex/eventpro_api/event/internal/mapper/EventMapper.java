package com.ex.eventpro_api.event.internal.mapper;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.response.EventResponse;
import com.ex.eventpro_api.event.internal.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)

    @Mapping(target = "status", constant = "PUBLISHED")
    Event toEntity(EventCreateRequest request);

    EventResponse toResponse(Event event);
}