package com.ex.eventpro_api.payment.internal.entity;

import com.ex.eventpro_api.payment.internal.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                // L'index sur status est utile pour les rapports (ex: tous les paiements SUCCESS)
                @Index(name = "idx_payment_status", columnList = "status")
                // Les index sur idempotencyKey et reference sont créés automatiquement par @Column(unique = true)
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'identifiant de la réservation est obligatoire")
    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @NotBlank(message = "La clé d'idempotence est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @NotBlank(message = "La référence est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    // ❌ PAS DE SETTER GLOBAL : On protège le statut
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // --- AUDIT ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ==========================================
    // LOGIQUE MÉTIER (Domain Driven Design)
    // ==========================================

    /**
     * Marque le paiement comme réussi.
     */
    public void markAsSuccess() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Seul un paiement en attente peut être validé.");
        }
        this.status = PaymentStatus.SUCCESS;
    }

    /**
     * Marque le paiement comme échoué.
     */
    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }
}