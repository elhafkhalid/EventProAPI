package com.ex.eventpro_api.user.api.dto;

import lombok.Builder;

@Builder
public record OrganizerDto(
        Long id,
        String name,
        String email,
        String organisationName
) {}