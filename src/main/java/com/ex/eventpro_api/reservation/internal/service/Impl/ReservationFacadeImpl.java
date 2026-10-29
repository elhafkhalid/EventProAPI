package com.ex.eventpro_api.reservation.internal.service.Impl;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.reservation.api.ReservationFacade;
import com.ex.eventpro_api.reservation.api.dto.ReservationDto;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.enums.ReservationStatus;
import com.ex.eventpro_api.reservation.internal.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationFacadeImpl implements ReservationFacade {

    private final ReservationRepository reservationRepository;

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
    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'ID : " + reservationId));

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }
}