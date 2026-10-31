package com.ex.eventpro_api.event.internal.service.Impl;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.response.EventResponse;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.internal.mapper.EventMapper;
import com.ex.eventpro_api.event.internal.repository.EventRepository;
import com.ex.eventpro_api.event.internal.service.EventCategoryService;
import com.ex.eventpro_api.event.internal.service.EventService;
import com.ex.eventpro_api.user.api.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserFacade userFacade;

    // On injecte le nouveau service intra-module
    private final EventCategoryService eventCategoryService;


    @Override
    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        // 1. Valider l'organisateur (Inter-module)
        userFacade.getOrganizerById(request.organizerId());

        // 2. Construire l'événement (Mapper)
        Event event = eventMapper.toEntity(request);

        // 3. Valider les règles métier internes (Entity)
        event.validateDates();

        // 4. Déléguer la construction des catégories (Intra-module service)
        List<TicketCategoriesPerEvent> categories = eventCategoryService.buildAndValidateCategories(request, event);
        event.setCategories(categories);

        // 5. Sauvegarder et renvoyer la réponse
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }


    @Override
    @Transactional
    public void publishEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'ID : " + eventId));

        // Délégation de la validation à l'entité
        event.publish();

        // Sauvegarde optionnelle (le @Transactional fait le dirty checking, mais on le laisse pour la clarté)
        eventRepository.save(event);
    }
}