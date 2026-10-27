package com.ex.eventpro_api.service.impl;

import com.ex.eventpro_api.dto.request.ReservationLineRequest;
import com.ex.eventpro_api.dto.request.ReservationRequest;
import com.ex.eventpro_api.dto.response.ReservationResponse;
import com.ex.eventpro_api.entity.*;
import com.ex.eventpro_api.enums.ReservationStatus;
import com.ex.eventpro_api.exception.ResourceNotFoundException;
import com.ex.eventpro_api.mapper.ReservationMapper;
import com.ex.eventpro_api.repository.PromotionRepository;
import com.ex.eventpro_api.repository.ReservationRepository;
import com.ex.eventpro_api.repository.UserRepository;
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

    private final TicketInventoryService inventoryService;
    private final PricingService pricingService;

    private final ReservationMapper reservationMapper;

    @Transactional
    @Override
    public ReservationResponse createReservation(ReservationRequest request) {

        Client client = (Client) userRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'ID : " + request.clientId()));


        Reservation reservation = Reservation.builder()
                .reference("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .client(client)
                .creationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMinutes(10))
                .status(ReservationStatus.PENDING_PAYMENT)
                .totalAmount(BigDecimal.ZERO)
                .build();


        if (request.promotionCode() != null && !request.promotionCode().isEmpty()) {
            Promotion promo = promotionRepository.findByCode(request.promotionCode())
                    .orElseThrow(() -> new IllegalArgumentException("Code promo invalide."));
            reservation.setPromotion(promo);
        }


        List<ReservationLine> lines = new ArrayList<>();
        for (ReservationLineRequest lineReq : request.lines()) {

            TicketCategoriesPerEvent stock = inventoryService.lockAndDecrementStock(
                    lineReq.ticketCategoriesPerEventId(),
                    lineReq.quantity()
            );

            Event event = stock.getEvent();
            if (!event.isOpenForSales()) {
                throw new IllegalArgumentException("Les ventes pour l'événement '" + event.getTitle() + "' ne sont pas ouvertes ou sont terminées.");
            }


            ReservationLine line = ReservationLine.builder()
                    .reservation(reservation)
                    .category(stock)
                    .quantity(lineReq.quantity())
                    .fixedUnitPrice(stock.getUnitPrice())
                    .subTotal(stock.getUnitPrice()
                            .multiply(BigDecimal.valueOf(lineReq.quantity())))
                    .build();

            lines.add(line);

        }

        reservation.setLines(lines);

        BigDecimal finalTotal = pricingService.calculateTotal(lines, request.promotionCode());
        reservation.setTotalAmount(finalTotal);

        reservationRepository.save(reservation);

        return reservationMapper.toResponse(reservation);

    }
}
