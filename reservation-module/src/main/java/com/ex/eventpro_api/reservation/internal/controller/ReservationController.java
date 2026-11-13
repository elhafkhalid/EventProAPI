package com.ex.eventpro_api.reservation.internal.controller;

import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.dto.response.ReservationResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.TopAmountResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.TopTicketsClientResponse;
import com.ex.eventpro_api.reservation.internal.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Renvoie 201 Created
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @GetMapping("/client/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponse> getClientReservations(@PathVariable Long clientId) {
        return reservationService.getClientReservations(clientId);
    }

    @GetMapping("/top_amount")
    @ResponseStatus(HttpStatus.OK)
    public TopAmountResponse findTopAmountClient() {
        return reservationService.findTopAmountClient();
    }

    @GetMapping("/top_tickets")
    @ResponseStatus(HttpStatus.OK)
    public TopTicketsClientResponse findTopTicketClient() {
        return reservationService.findTopTicketClient();
    }


    /// mise en situation 1
    @GetMapping("/recherche")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponse> rechercherReser(
            @RequestParam String email,
            @RequestParam String nomCategorie,
            @RequestParam(defaultValue = "1") int quantiteMin,
            @RequestParam Long eventId) {

        return reservationService.findReservationByEmailAndCateg(
                email,
                nomCategorie,
                quantiteMin,
                eventId
        );
    }
}