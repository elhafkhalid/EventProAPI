package com.ex.eventpro_api.reservation.internal.service.Impl;

import com.ex.eventpro_api.core.exception.ResourceNotFoundException;
import com.ex.eventpro_api.reservation.internal.dto.response.TicketScanResponse;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import com.ex.eventpro_api.reservation.internal.entity.Ticket;
import com.ex.eventpro_api.reservation.internal.enums.TicketStatus;
import com.ex.eventpro_api.reservation.internal.repository.TicketRepository;
import com.ex.eventpro_api.reservation.internal.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public void generateTickets(Reservation reservation) {

        // 1. La magie du Stream (Remplacement des 2 boucles for)
        List<Ticket> allTickets = reservation.getLines().stream()
                // Pour chaque ligne, on génère un flux de N billets (N = quantity)
                .flatMap(line -> IntStream.range(0, line.getQuantity())
                        .mapToObj(i -> buildTicket(line))
                )
                // On rassemble tous les flux en une seule liste
                .toList();

        // 2. On sauvegarde tous les billets d'un coup
        ticketRepository.saveAll(allTickets);
    }

    // Dans TicketServiceImpl.java
    @Override
    @Transactional
    public TicketScanResponse scanTicket(String ticketCode) {
        Ticket ticket = ticketRepository.findByCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Billet introuvable avec le code : " + ticketCode));

        // L'entité fait la vérification (si déjà scanné, elle lève une exception)
        ticket.scan();
        Ticket savedTicket = ticketRepository.save(ticket);

        // On renvoie les infos utiles à l'agent
        return TicketScanResponse.builder()
                .ticketCode(savedTicket.getCode())
                .status(savedTicket.getStatus().name())
                .scanDate(savedTicket.getScanDate())
                .reservationId(savedTicket.getReservationLine().getReservation().getId())
                .build();
    }

    // ==========================================
    // MÉTHODES PRIVÉES
    // ==========================================

    private Ticket buildTicket(ReservationLine line) {
        return Ticket.builder()
                .code("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .reservationLine(line)
                .status(TicketStatus.VALID)
                .build();
    }
}