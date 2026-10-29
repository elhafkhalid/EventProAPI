package com.ex.eventpro_api.reservation.service.Impl;

import com.ex.eventpro_api.reservation.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.dto.response.ReservationResponse;
import com.ex.eventpro_api.reservation.entity.Reservation;
import com.ex.eventpro_api.reservation.entity.ReservationLine;
import com.ex.eventpro_api.user.entity.Client;
import com.ex.eventpro_api.user.repository.UserRepository;
import com.ex.eventpro_api.promotion.entity.Promotion;
import com.ex.eventpro_api.promotion.repository.PromotionRepository;
import com.ex.eventpro_api.reservation.enums.ReservationStatus;
import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.reservation.mapper.ReservationMapper;
import com.ex.eventpro_api.reservation.repository.ReservationRepository;
import com.ex.eventpro_api.reservation.service.ReservationLineService; // Le nouveau service
import com.ex.eventpro_api.reservation.service.ReservationService;
import com.ex.eventpro_api.reservation.service.PricingService; // Le service de prix
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final ReservationMapper reservationMapper;

    // On injecte nos 3 sous-services
    private final ReservationLineService lineService;
    private final PricingService pricingService;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        // 1. Récupérer le client
        Client client = getClient(request.clientId());

        // 2. Créer le panier de base
        Reservation reservation = buildReservation(client);

        // 3. Récupérer la promotion
        Promotion promo = getPromotion(request.promotionCode());

        // 4. DÉLÉGATION : Faire traiter les lignes par le nouveau service
        List<ReservationLine> lines = lineService.processLines(request, reservation, promo);
        reservation.setLines(lines);

        // 5. DÉLÉGATION : Calculer le total
        BigDecimal finalTotal = pricingService.calculateTotal(lines, request.promotionCode());
        reservation.setTotalAmount(finalTotal);

        // 6. Assigner la promo et sauvegarder
        if (promo != null) {
            reservation.setPromotion(promo);
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponse(savedReservation);
    }

    // --- Méthodes privées restantes ---

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
}