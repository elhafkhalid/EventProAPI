package com.ex.eventpro_api.reservation.internal.controller;

import com.ex.eventpro_api.reservation.internal.dto.response.TicketResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.TicketScanResponse;
import com.ex.eventpro_api.reservation.internal.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/{code}/scan")
    @ResponseStatus(HttpStatus.OK)
    public TicketScanResponse scanTicket(@PathVariable String code) {
        // On renvoie le DTO propre
        return ticketService.scanTicket(code);
    }

    @GetMapping("/reservation/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketResponse> getTicketsByReservation(@PathVariable Long reservationId) {
        return ticketService.getTicketsByReservation(reservationId);
    }
}