package com.ex.eventpro_api.entity;

import com.ex.eventpro_api.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Une référence unique générée par le système (ex: "RES-123456")
    @Column(nullable = false, unique = true)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime expirationDate; // Création + 10 minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // Optionnel : La promotion appliquée
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    // Une réservation a plusieurs lignes
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationLine> lines;
}