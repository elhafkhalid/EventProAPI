package com.ex.eventpro_api.user.api;

import com.ex.eventpro_api.user.api.dto.ClientDto;
import com.ex.eventpro_api.user.api.dto.OrganizerDto;
import com.ex.eventpro_api.user.internal.entity.Client;
import com.ex.eventpro_api.user.internal.entity.User;

import java.util.List;

public interface UserFacade {
    ClientDto getClientById(Long id);
    OrganizerDto getOrganizerById(Long id);
    List<ClientDto> getAllClients();
}