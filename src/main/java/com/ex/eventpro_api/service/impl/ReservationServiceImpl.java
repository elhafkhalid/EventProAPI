package com.ex.eventpro_api.service.impl;

import com.ex.eventpro_api.dto.request.ReservationLineRequest;
import com.ex.eventpro_api.dto.request.ReservationRequest;
import com.ex.eventpro_api.dto.response.ReservationResponse;
import com.ex.eventpro_api.entity.*;
import com.ex.eventpro_api.enums.ReservationStatus;
import com.ex.eventpro_api.exception.ResourceNotFoundException;
import com.ex.eventpro_api.mapper.ReservationMapper;
import com.ex.eventpro_api.repository.*;
import com.ex.eventpro_api.service.PricingService;
import com.ex.eventpro_api.service.ReservationService;
import com.ex.eventpro_api.service.TicketInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PromotionRepository promotionRepository;
    private final ReservationLineRepository reservationLineRepository;
    private final EventRepository eventRepository;

    private final TicketInventoryService inventoryService;
    private final PricingService pricingService;

    private final ReservationMapper reservationMapper;

    @Transactional
    @Override
    public ReservationResponse createReservation(ReservationRequest request) {

        // 1. Récupérer le client
        Client client = getClient(request.clientId());

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Événement introuvable avec l'ID : " + request.eventId()));

        // 2. Créer le panier de base
        Reservation reservation = buildReservation(client);

        // 3. Récupérer la promotion (si elle existe)
        Promotion promo = getPromotion(request.promotionCode());

        // 4. Traiter les lignes
        List<ReservationLine> lines = processLines(request, reservation, promo, request.eventId());
        reservation.setLines(lines);

        // 5. Assigner la promo au panier maintenant qu'on sait qu'elle est valide
        if (promo != null) {
            reservation.setPromotion(promo);
        }

        // 6. Calculer le total final
        BigDecimal finalTotal = pricingService.calculateTotal(lines, request.promotionCode());
        reservation.setTotalAmount(finalTotal);

        // 7. Sauvegarder et renvoyer
        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponse(savedReservation);
    }

    // --- MÉTHODES PRIVÉES ---

    private Client getClient(Long clientId) {
        return (Client) userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'ID : " + clientId));
    }

    private Reservation buildReservation(Client client) {
        return Reservation.builder()
                .reference("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .client(client)
                .creationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMinutes(10))
                .status(ReservationStatus.PENDING_PAYMENT)
                .totalAmount(BigDecimal.ZERO)
                .build();
    }

    private Promotion getPromotion(String promotionCode) {
        if (promotionCode != null && !promotionCode.isEmpty()) {
            return promotionRepository.findByCode(promotionCode)
                    .orElseThrow(() -> new IllegalArgumentException("Code promo invalide."));
        }
        return null;
    }

    private List<ReservationLine> processLines(ReservationRequest request, Reservation reservation, Promotion promo, Long eventId) {
        List<ReservationLine> lines = new ArrayList<>();

        for (ReservationLineRequest lineReq : request.lines()) {
            ReservationLine line = createReservationLine(reservation, lineReq, request.clientId(), promo, eventId);
            lines.add(line);
        }

        return lines;
    }

    private ReservationLine createReservationLine(Reservation reservation, ReservationLineRequest lineReq, Long clientId, Promotion promo, Long eventId) {
        // 1. Verrouiller le stock
        TicketCategoriesPerEvent stock = inventoryService.lockAndDecrementStock(
                lineReq.ticketCategoriesPerEventId(),
                lineReq.quantity()
        );

        if (!stock.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("La catégorie ID " + lineReq.ticketCategoriesPerEventId() + " n'appartient pas à l'événement spécifié.");
        }

        // 2. VÉRIFICATION : La promotion appartient-elle à cet événement ?
        if (promo != null && !promo.getEvent().getId().equals(stock.getEvent().getId())) {
            throw new IllegalArgumentException("Le code promo '" + promo.getCode() + "' n'est pas valide pour cet événement.");
        }

        // 3. Vérifier le quota max par client
        checkClientQuota(clientId, stock, lineReq.quantity());

        // 4. Vérifier si l'événement est ouvert aux ventes
        checkEventSalesStatus(stock.getEvent());

        // 5. Construire la ligne
        return ReservationLine.builder()
                .reservation(reservation)
                .category(stock)
                .quantity(lineReq.quantity())
                .fixedUnitPrice(stock.getUnitPrice())
                .subTotal(stock.getUnitPrice().multiply(BigDecimal.valueOf(lineReq.quantity())))
                .build();
    }

    private void checkClientQuota(Long clientId, TicketCategoriesPerEvent stock, Integer requestedQuantity) {
        Integer alreadyBought = reservationLineRepository.countTotalQuantityBoughtByClientForCategory(
                clientId,
                stock.getId()
        );

        if (alreadyBought + requestedQuantity > stock.getMaxQuotaPerClient()) {
            throw new IllegalArgumentException(
                    "Limite d'achat dépassée pour la catégorie " + stock.getCategory().getName() +
                            ". Maximum autorisé : " + stock.getMaxQuotaPerClient() +
                            ". Vous en avez déjà réservé : " + alreadyBought
            );
        }
    }

    private void checkEventSalesStatus(Event event) {
        if (!event.isOpenForSales()) {
            throw new IllegalArgumentException("Les ventes pour l'événement '" + event.getTitle() + "' ne sont pas ouvertes ou sont terminées.");
        }
    }
}