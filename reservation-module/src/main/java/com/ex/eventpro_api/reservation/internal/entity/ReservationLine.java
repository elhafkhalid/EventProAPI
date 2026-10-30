package com.ex.eventpro_api.reservation.internal.entity;

import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "reservation_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "ticket_categories_per_event_id", nullable = false)
    private Long ticketCategoriesPerEventId;

    @Column(nullable = false)
    private Integer quantity;

    // On fige le prix au moment de l'achat
    @Column(nullable = false)
    private BigDecimal fixedUnitPrice;

    @Column(nullable = false)
    private BigDecimal subTotal;
}