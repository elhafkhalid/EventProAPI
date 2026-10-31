package com.ex.eventpro_api.event.internal.service.Impl;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.request.TicketCategoryRequest;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.internal.entity.TicketCategory;
import com.ex.eventpro_api.event.internal.repository.TicketCategoryRepository;
import com.ex.eventpro_api.event.internal.service.EventCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;

    @Override
    public List<TicketCategoriesPerEvent> buildAndValidateCategories(EventCreateRequest request, Event event) {

        // 1. Guard Clause (Sécurité de base)
        if (request.categories() == null || request.categories().isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Validation globale de la capacité (Avant même de créer les objets)
        validateTotalCapacity(request);

        // 3. Transformation (Approche fonctionnelle avec les Streams)
        return request.categories().stream()
                .map(catReq -> buildCategoryConfig(event, catReq))
                .toList(); // .toList() est disponible depuis Java 16
    }

    // ==========================================
    // MÉTHODES PRIVÉES (Les petites briques)
    // ==========================================

    /**
     * Calcule la somme totale des places demandées et vérifie qu'elle ne dépasse pas la capacité.
     */
    private void validateTotalCapacity(EventCreateRequest request) {
        int totalQuantitySum = request.categories().stream()
                .mapToInt(TicketCategoryRequest::totalQuantity)
                .sum();

        checkCapacity(totalQuantitySum, request.capacity());
    }

    /**
     * Construit une ligne de configuration (Entité) à partir d'une requête.
     */
    private TicketCategoriesPerEvent buildCategoryConfig(Event event, TicketCategoryRequest catReq) {
        TicketCategory category = getOrCreateTicketCategory(catReq.name());

        return TicketCategoriesPerEvent.builder()
                .event(event)
                .category(category)
                .unitPrice(catReq.unitPrice())
                .totalQuantity(catReq.totalQuantity())
                .maxQuotaPerClient(catReq.maxQuotaPerClient())
                .build();
    }

    /**
     * Cherche une catégorie par son nom. Si elle n'existe pas, la crée.
     */
    private TicketCategory getOrCreateTicketCategory(String categoryName) {
        return ticketCategoryRepository.findByNameIgnoreCase(categoryName)
                .orElseGet(() -> ticketCategoryRepository.save(
                        TicketCategory.builder().name(categoryName).build()
                ));
    }

    /**
     * Vérifie la règle métier de capacité.
     */
    private void checkCapacity(int totalQuantitySum, int capacity) {
        if (totalQuantitySum > capacity) {
            throw new IllegalArgumentException("La somme des places (" + totalQuantitySum + ") dépasse la capacité (" + capacity + ").");
        }
    }
}