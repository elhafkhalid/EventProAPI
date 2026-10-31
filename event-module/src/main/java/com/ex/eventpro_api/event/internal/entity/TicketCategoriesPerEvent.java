package com.ex.eventpro_api.event.internal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ticket_categories_per_event",
        indexes = {
                @Index(name = "idx_ticket_event", columnList = "event_id"),
                @Index(name = "idx_ticket_category", columnList = "category_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCategoriesPerEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Intra-module : On garde l'objet Event
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Intra-module : On garde l'objet TicketCategory
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private TicketCategory category;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix ne peut pas être négatif")
    @Column(nullable = false)
    private BigDecimal unitPrice;

    @NotNull(message = "La quantité totale est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @Column(nullable = false)
    private Integer totalQuantity;

    @NotNull(message = "Le quota par client est obligatoire")
    @Min(value = 1, message = "Le quota doit être d'au moins 1")
    @Column(nullable = false)
    private Integer maxQuotaPerClient;

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
     * Vérifie la cohérence des données (ex: le quota ne peut pas dépasser le stock total)
     */
    @AssertTrue(message = "Le quota par client ne peut pas être supérieur à la quantité totale")
    private boolean isQuotaLogicValid() {
        if (maxQuotaPerClient == null || totalQuantity == null) return true;
        return maxQuotaPerClient <= totalQuantity;
    }

    /**
     * Vérifie s'il reste assez de places pour la quantité demandée.
     */
    public boolean hasEnoughStock(int requestedQuantity) {
        return this.totalQuantity >= requestedQuantity;
    }

    /**
     * Décrémente le stock de manière sécurisée.
     * Lance une exception si le stock est insuffisant.
     */
    public void decrementStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("Stock insuffisant pour la catégorie " + this.category.getName() + ". Restant : " + this.totalQuantity);
        }
        this.totalQuantity -= quantity;
    }

    /**
     * Remet le stock en cas d'annulation ou d'expiration.
     */
    public void releaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité à libérer doit être positive.");
        }
        this.totalQuantity += quantity;
    }
}