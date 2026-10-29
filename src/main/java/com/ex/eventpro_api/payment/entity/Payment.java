package com.ex.eventpro_api.payment.entity;

import com.ex.eventpro_api.payment.enums.PaymentStatus;
import com.ex.eventpro_api.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le lien vers la réservation (Une réservation a un seul paiement)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // La clé d'idempotence (unique) pour éviter les doubles débits
    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    // Une référence générée pour le reçu (ex: PAY-12345)
    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
}