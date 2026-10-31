package com.ex.eventpro_api.reservation.internal.service.Impl;

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

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        // 1. Valider le client via la Façade
        userFacade.getClientById(request.clientId());

        // 2. Récupérer la promo via la Façade (si elle existe)
        PromotionDto promoDto = null;
        if (request.promotionCode() != null && !request.promotionCode().isEmpty()) {
            promoDto = promotionFacade.getPromotionByCode(request.promotionCode());

            if (promoDto != null && !promoDto.eventId().equals(request.eventId())) {
                throw new IllegalArgumentException("Le code promo '" + request.promotionCode() + "' n'est pas valide pour cet événement.");
            }

        }

        // 3. Créer le panier
        Reservation reservation = Reservation.builder()
                .reference("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .clientId(request.clientId()) // On met l'ID direct !
                .creationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMinutes(10))
                .status(ReservationStatus.PENDING_PAYMENT)
                .totalAmount(BigDecimal.ZERO)
                .promotionId(promoDto != null ? promoDto.id() : null) // On met l'ID de la promo !
                .build();

        // 4. Traiter les lignes (via le LineService)
        List<ReservationLine> lines = lineService.processLines(request, reservation, promoDto);
        reservation.setLines(lines);

        // 5. Calculer le total
        BigDecimal finalTotal = pricingService.calculateTotal(lines, request.promotionCode());
        reservation.setTotalAmount(finalTotal);

        // 6. Sauvegarder
        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponse(savedReservation);

    }
}