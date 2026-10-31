package com.ex.eventpro_api.user.internal.service;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.user.api.UserFacade;
import com.ex.eventpro_api.user.api.dto.ClientDto;
import com.ex.eventpro_api.user.internal.entity.Client;
import com.ex.eventpro_api.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;

    @Override
    public ClientDto getClientById(Long id) {
        Client client = (Client) userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'ID : " + id));

        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .build();
    }

    @Override
    public void getOrganizerById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisateur introuvable avec l'ID : " + id));
    }
}