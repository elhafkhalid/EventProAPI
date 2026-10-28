package com.ex.eventpro_api.service.impl;

import com.ex.eventpro_api.dto.request.EventCreateRequest;
import com.ex.eventpro_api.dto.request.TicketCategoryRequest;
import com.ex.eventpro_api.dto.response.EventResponse;
import com.ex.eventpro_api.entity.Event;
import com.ex.eventpro_api.entity.Organizer;
import com.ex.eventpro_api.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.entity.TicketCategory;
import com.ex.eventpro_api.exception.ResourceNotFoundException;
import com.ex.eventpro_api.mapper.EventMapper;
import com.ex.eventpro_api.repository.EventRepository;
import com.ex.eventpro_api.repository.TicketCategoryRepository;
import com.ex.eventpro_api.repository.UserRepository;
import com.ex.eventpro_api.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final EventMapper eventMapper; // On injecte ton Mapper

    @Override
    @Transactional // Tout réussit ou tout est annulé
    public EventResponse createEvent(EventCreateRequest request) {

        // 1. Vérifier que l'organisateur existe (Règle métier)
        Organizer organizer = (Organizer) userRepository.findById(request.organizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organisateur introuvable avec l'ID : " + request.organizerId()));

        // 2. MAPPER : On utilise toEntity (il copie le titre, les dates, et met DRAFT)
        Event event = eventMapper.toEntity(request);

        // 3. SERVICE : On lie l'organisateur (que le Mapper a ignoré)
        event.setOrganizer(organizer);

        // 4. SERVICE : Gestion des catégories (que le Mapper a ignorées)
        List<TicketCategoriesPerEvent> categoriesPerEvent = new ArrayList<>();
        int totalQuantitySum = 0; // Pour vérifier la capacité globale

        if (request.categories() != null) {
            for (TicketCategoryRequest catReq : request.categories()) {

                // 4.1 Vérification de la capacité de la salle
                totalQuantitySum += catReq.totalQuantity();
                if (totalQuantitySum > request.capacity()) {
                    throw new IllegalArgumentException("La somme des places (" + totalQuantitySum + ") dépasse la capacité de l'événement (" + request.capacity() + ").");
                }

                // 4.2 Chercher la catégorie globale (ex: "VIP"). Si elle n'existe pas, on la crée.
                TicketCategory category = ticketCategoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(catReq.name()))
                        .findFirst()
                        .orElseGet(() -> ticketCategoryRepository.save(
                                TicketCategory.builder().name(catReq.name()).build()
                        ));

                // 4.3 Créer la ligne de liaison avec le prix et le stock
                TicketCategoriesPerEvent config = TicketCategoriesPerEvent.builder()
                        .event(event)
                        .category(category)
                        .unitPrice(catReq.unitPrice())
                        .totalQuantity(catReq.totalQuantity())
                        .maxQuotaPerClient(catReq.maxQuotaPerClient())
                        .build();

                categoriesPerEvent.add(config);
            }
        }

        // 5. On attache les catégories à l'événement
        event.setCategories(categoriesPerEvent);

        // 6. Sauvegarde en base (grâce au CascadeType.ALL de l'entité Event)
        Event savedEvent = eventRepository.save(event);

        // 7. MAPPER : On utilise toResponse pour renvoyer le reçu au client
        return eventMapper.toResponse(savedEvent);
    }
}