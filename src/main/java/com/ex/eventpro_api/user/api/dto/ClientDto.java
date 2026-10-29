package com.ex.eventpro_api.user.api.dto;

import lombok.Builder;

@Builder
public record ClientDto(
        Long id,
        String name,
        String email,
        String phone
) {}