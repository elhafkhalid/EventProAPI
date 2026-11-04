package com.ex.eventpro_api.user.internal.facade;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.user.api.UserFacade;
import com.ex.eventpro_api.user.api.dto.ClientDto;
import com.ex.eventpro_api.user.api.dto.OrganizerDto;
import com.ex.eventpro_api.user.internal.entity.Client;
import com.ex.eventpro_api.user.internal.entity.Organizer;
import com.ex.eventpro_api.user.internal.entity.User;
import com.ex.eventpro_api.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;

    @Override
    public ClientDto getClientById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'ID : " + id));

        if (!(user instanceof Client))
            throw new IllegalArgumentException("L'ID fourni ne correspond pas à un client.");

        Client client = (Client) user;
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .build();
    }

    @Override
    public OrganizerDto getOrganizerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur intrv avec LID : " + id));

        if (!(user instanceof Organizer))
            throw new IllegalArgumentException("L'ID fourni ne correspond pas à un Organisateur.");

        return OrganizerDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

    }
}