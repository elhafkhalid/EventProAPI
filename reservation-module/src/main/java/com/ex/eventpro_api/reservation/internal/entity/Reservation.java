package com.ex.eventpro_api.reservation.internal.entity;

import com.ex.eventpro_api.reservation.internal.enums.ReservationStatus;
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
import java.util.List;

@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "idx_reservation_client", columnList = "client_id"),
                @Index(name = "idx_reservation_status", columnList = "status") // Très important pour le Scheduler !
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La référence est obligatoire")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @NotNull(message = "L'identifiant du client est obligatoire")
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime creationDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    // ❌ PAS DE SETTER ! On utilise les méthodes confirm(), cancel(), expire()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "promotion_id")
    private Long promotionId;

    @NotNull
    @DecimalMin(value = "0.0", message = "Le montant total ne peut pas être négatif")
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // ✅ @Setter OBLIGATOIRE ICI pour lier les lignes (CascadeType.ALL)
    @Setter
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationLine> lines;

    // --- AUDIT ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ==========================================
    // LOGIQUE MÉTIER (Rich Domain Model)
    // ==========================================

    /**
     * Vérifie si la réservation a dépassé les 10 minutes.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationDate);
    }

    /**
     * Vérifie si la réservation est en attente de paiement.
     */
    public boolean isPendingPayment() {
        return this.status == ReservationStatus.PENDING_PAYMENT;
    }

    /**
     * Confirme la réservation (après paiement réussi).
     */
    public void confirm() {
        if (!isPendingPayment()) {
            throw new IllegalStateException("Seule une réservation en attente de paiement peut être confirmée.");
        }
        if (isExpired()) {
            throw new IllegalStateException("Impossible de confirmer une réservation expirée.");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    /**
     * Expire la réservation (appelé par le Scheduler).
     */
    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }

    /**
     * Annule la réservation (appelé par le client).
     */
    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }


    public void updateTotalAmount(BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le total ne peut pas être négatif.");
        }
        this.totalAmount = total;
    }
}