package com.ex.eventpro_api.reservation.internal.facade;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.reservation.api.ReservationCount;
import com.ex.eventpro_api.reservation.api.ReservationFacade;
import com.ex.eventpro_api.reservation.api.dto.ReservationDto;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.repository.ReservationRepository;
import com.ex.eventpro_api.reservation.internal.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationFacadeImpl implements ReservationFacade {

    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;

    @Override
    public ReservationDto getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'ID : " + reservationId));

        return ReservationDto.builder()
                .id(reservation.getId())
                .clientId(reservation.getClientId())
                .totalAmount(reservation.getTotalAmount())
                .status(reservation.getStatus().name())
                .expirationDate(reservation.getExpirationDate())
                .build();
    }

    @Override
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'ID : " + reservationId));

        reservation.confirm();
        reservationRepository.save(reservation);
        ticketService.generateTickets(reservation);
    }

    @Override
    public List<ReservationCount> findClientsWithReservations() {
        return reservationRepository.findClientsWithReservations();
    }


}