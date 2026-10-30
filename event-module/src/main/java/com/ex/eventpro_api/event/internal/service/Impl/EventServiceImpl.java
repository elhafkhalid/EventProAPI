package com.ex.eventpro_api.event.internal.service.Impl;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.request.TicketCategoryRequest;
import com.ex.eventpro_api.event.internal.dto.response.EventResponse;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.internal.entity.TicketCategory;
import com.ex.eventpro_api.event.internal.mapper.EventMapper;
import com.ex.eventpro_api.event.internal.repository.EventRepository;
import com.ex.eventpro_api.event.internal.repository.TicketCategoryRepository;
import com.ex.eventpro_api.event.internal.service.EventService;
import com.ex.eventpro_api.user.api.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final EventMapper eventMapper;
    private final UserFacade userFacade;

    @Override
    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {

        // 1. Valider l'organisateur
        validateOrganizer(request.organizerId());

        // 2. Construire l'événement de base
        Event event = buildEvent(request);

        // 3. Construire et valider les catégories
        List<TicketCategoriesPerEvent> categories = buildCategories(request, event);

        // 4. Lier les catégories à l'événement
        event.setCategories(categories);

        // 5. Sauvegarder et renvoyer la réponse
        return saveAndBuildResponse(event);
    }


    private void validateOrganizer(Long organizerId) {
        // Si l'organisateur n'existe pas, la Façade lève une exception
        userFacade.valOrganizerById(organizerId);
    }


    private Event buildEvent(EventCreateRequest request) {
        return eventMapper.toEntity(request);
    }


    private List<TicketCategoriesPerEvent> buildCategories(EventCreateRequest request, Event event) {
        List<TicketCategoriesPerEvent> categoriesPerEvent = new ArrayList<>();

        if (request.categories() == null) {
            return categoriesPerEvent;
        }

        int totalQuantitySum = 0;
        for (TicketCategoryRequest catReq : request.categories()) {

            // 3.1 Vérification de la capacité
            totalQuantitySum += catReq.totalQuantity();
            checkCapacity(totalQuantitySum, request.capacity());

            // 3.2 Chercher ou créer la catégorie globale
            TicketCategory category = findOrCreateTicketCategory(catReq.name());

            // 3.3 Créer la ligne de liaison
            TicketCategoriesPerEvent config = buildTicketCategoriesPerEvent(event, category, catReq);
            categoriesPerEvent.add(config);
        }

        return categoriesPerEvent;
    }


    private void checkCapacity(int totalQuantitySum, int capacity) {
        if (totalQuantitySum > capacity) {
            throw new IllegalArgumentException("La somme des places (" + totalQuantitySum + ") dépasse la capacité de l'événement (" + capacity + ").");
        }
    }


    private TicketCategory findOrCreateTicketCategory(String categoryName) {
        return ticketCategoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElseGet(() -> ticketCategoryRepository.save(
                        TicketCategory.builder().name(categoryName).build()
                ));
    }


    private TicketCategoriesPerEvent buildTicketCategoriesPerEvent(Event event, TicketCategory category, TicketCategoryRequest catReq) {
        return TicketCategoriesPerEvent.builder()
                .event(event)
                .category(category)
                .unitPrice(catReq.unitPrice())
                .totalQuantity(catReq.totalQuantity())
                .maxQuotaPerClient(catReq.maxQuotaPerClient())
                .build();
    }


    private EventResponse saveAndBuildResponse(Event event) {
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }
}