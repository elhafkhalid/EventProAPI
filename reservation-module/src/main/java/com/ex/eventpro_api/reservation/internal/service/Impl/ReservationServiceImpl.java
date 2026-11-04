package com.ex.eventpro_api.reservation.internal.service.Impl;

import com.ex.eventpro_api.event.api.EventFacade;
import com.ex.eventpro_api.event.api.dto.EventDto;
import com.ex.eventpro_api.promotion.api.PromotionFacade;
import com.ex.eventpro_api.promotion.api.dto.PromotionDto;
import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.dto.response.ReservationResponse;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import com.ex.eventpro_api.reservation.internal.enums.ReservationStatus;
import com.ex.eventpro_api.reservation.internal.mapper.ReservationMapper;
import com.ex.eventpro_api.reservation.internal.repository.ReservationRepository;
import com.ex.eventpro_api.reservation.internal.service.ReservationLineService;
import com.ex.eventpro_api.reservation.internal.service.ReservationService;
import com.ex.eventpro_api.reservation.internal.service.PricingService;
import com.ex.eventpro_api.user.api.UserFacade;
import com.ex.eventpro_api.user.api.dto.ClientDto;
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
    private final ReservationLineService lineService;
    private final PricingService pricingService;
    private final ReservationMapper reservationMapper;

    private final UserFacade userFacade;
    private final PromotionFacade promotionFacade;
    private final EventFacade eventFacade;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        // 1. Les validations (Si l'une échoue, on s'arrête ici)
        ClientDto clientDto = validateClient(request.clientId());
        EventDto eventDto = validateEvent(request.eventId());

        // 2. La promotion (Validation et récupération)
        PromotionDto promoDto = getValidatedPromotion(request.promotionCode(), request.eventId());

        // 3. Création du panier
        Reservation reservation = buildReservation(request, promoDto);

        // 4. Traitement des billets (Verrou, Quota, Prix)
        processReservationLines(request, reservation, promoDto);

        // 5. Calcul du total
        calculateAndApplyTotal(request.promotionCode(), reservation);

        // 6. Incrémentation de la promo (Seulement si tout est OK)
        incrementPromotionUsageIfPresent(request.promotionCode());

        // 7. Sauvegarde et retour
        return saveAndMapResponse(reservation);
    }

    // ==========================================
    // MÉTHODES PRIVÉES (Le découpage Expert)
    // ==========================================

    private ClientDto validateClient(Long clientId) {
        return userFacade.getClientById(clientId); // Lève une exception 404 si introuvable
    }

    private EventDto validateEvent(Long eventId) {
        return eventFacade.getEventById(eventId); // Lève une exception 404 si introuvable
    }

    private PromotionDto getValidatedPromotion(String promotionCode, Long eventId) {
        if (promotionCode == null || promotionCode.isEmpty()) {
            return null;
        }
        promotionFacade.validatePromotion(promotionCode, eventId); // Lève une 400 si invalide
        return promotionFacade.getPromotionByCode(promotionCode);
    }

    private Reservation buildReservation(ReservationRequest request, PromotionDto promoDto) {
        return Reservation.builder()
                .reference("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .clientId(request.clientId())
                .creationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMinutes(10))
                .status(ReservationStatus.PENDING_PAYMENT)
                .totalAmount(BigDecimal.ZERO)
                .promotionId(promoDto != null ? promoDto.id() : null)
                .build();
    }

    private void processReservationLines(ReservationRequest request, Reservation reservation, PromotionDto promoDto) {
        List<ReservationLine> lines = lineService.processLines(request, reservation, promoDto);
        reservation.setLines(lines);
    }

    private void calculateAndApplyTotal(String promotionCode, Reservation reservation) {
        BigDecimal finalTotal = pricingService.calculateTotal(reservation.getLines(), promotionCode);
        reservation.updateTotalAmount(finalTotal);
    }

    private void incrementPromotionUsageIfPresent(String promotionCode) {
        if (promotionCode != null && !promotionCode.isEmpty()) {
            promotionFacade.incrementUsage(promotionCode);
        }
    }

    private ReservationResponse saveAndMapResponse(Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponse(savedReservation);
    }
}