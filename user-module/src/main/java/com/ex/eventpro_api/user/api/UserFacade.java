package com.ex.eventpro_api.user.api;

import com.ex.eventpro_api.user.api.dto.ClientDto;
import com.ex.eventpro_api.user.api.dto.OrganizerDto;

public interface UserFacade {
    ClientDto getClientById(Long id);
    void getOrganizerById(Long id);
}