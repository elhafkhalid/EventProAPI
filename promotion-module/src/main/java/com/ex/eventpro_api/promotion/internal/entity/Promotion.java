package com.ex.eventpro_api.promotion.internal.entity;

import com.ex.eventpro_api.promotion.internal.enums.DiscountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "promotions",
        indexes = {
                @Index(name = "idx_promotion_event", columnList = "event_id"),
                @Index(name = "idx_promotion_code", columnList = "code")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La FK sans couplage
    @NotNull
    @Column(name = "event_id", nullable = false, unique = true)
    private Long eventId;

    // ❌ Plus de @Setter global : on protège les champs sensibles
    @Setter
    @NotBlank(message = "Le code promo est obligatoire")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Setter
    @NotNull
    @DecimalMin(value = "0.01", message = "La réduction doit être supérieure à 0")
    @Column(nullable = false)
    private BigDecimal discountValue;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Setter
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer usageLimit;

    // ❌ PAS DE SETTER ICI ! On utilise incrementUsage() à la place
    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

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
     * Vérifie que la promotion est utilisable MAINTENANT.
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) &&
                now.isBefore(endDate) &&
                usageCount < usageLimit;
    }

    /**
     * Incrémente le compteur d'utilisation de manière sécurisée.
     */
    public void incrementUsage() {
        if (this.usageCount >= this.usageLimit) {
            throw new IllegalArgumentException("Ce code promo a atteint sa limite d'utilisation.");
        }
        this.usageCount++;
    }

    /**
     * Calcule la réduction. (Correction de la faille financière)
     */
    public BigDecimal calculateDiscount(BigDecimal amount) {
        BigDecimal discount;
        if (discountType == DiscountType.PERCENTAGE) {
            // On calcule le pourcentage
            discount = amount.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else {
            // Montant fixe
            discount = discountValue;
        }

        // SÉCURITÉ : La réduction ne peut JAMAIS dépasser le montant total du panier
        return discount.min(amount);
    }

    public void validateDiscountValue() {
        if (discountType == DiscountType.PERCENTAGE && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Une réduction en pourcentage ne peut pas dépasser 100%.");
        }
    }

    /**
     * Validation de la cohérence temporelle.
     */
    public void validateDates() {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin.");
        }
    }



}