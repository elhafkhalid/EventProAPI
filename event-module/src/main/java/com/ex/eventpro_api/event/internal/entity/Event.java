package com.ex.eventpro_api.event.internal.entity;

import com.ex.eventpro_api.event.internal.enums.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "events",
        indexes = {
                @Index(name = "idx_event_organizer", columnList = "organizer_id"), // Index pour les requêtes par organisateur
                @Index(name = "idx_event_status", columnList = "status")            // Index pour filtrer les événements publiés
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Le lieu est obligatoire")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "La date de début est obligatoire")
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotNull(message = "L'ouverture des ventes est obligatoire")
    @Column(nullable = false)
    private LocalDateTime salesOpeningDate;

    @NotNull(message = "La fermeture des ventes est obligatoire")
    @Column(nullable = false)
    private LocalDateTime salesClosingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @NotNull
    @Min(value = 1, message = "La capacité doit être d'au moins 1 place")
    @Column(nullable = false)
    private Integer capacity;

    // La FK sans couplage : on garde l'ID, on ajoute NotNull et l'index
    @NotNull(message = "L'organisateur est obligatoire")
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketCategoriesPerEvent> categories;

    // --- AUDIT ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ==========================================
    // LOGIQUE MÉTIER (Méthodes explicites)
    // ==========================================

    public boolean isOpenForSales() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == EventStatus.PUBLISHED &&
                now.isAfter(this.salesOpeningDate) &&
                now.isBefore(this.salesClosingDate);
    }

    /**
     * Valide la cohérence temporelle de l'événement.
     * Cette méthode doit être appelée par le Service avant la sauvegarde.
     */
    public void validateDates() {
        if (startDate == null || endDate == null || salesOpeningDate == null || salesClosingDate == null) {
            throw new IllegalArgumentException("Toutes les dates doivent être renseignées.");
        }

        // 1. Le début doit être avant la fin
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début de l'événement doit être antérieure à la date de fin.");
        }

        // 2. L'ouverture des ventes doit être avant la fermeture
        if (salesOpeningDate.isAfter(salesClosingDate)) {
            throw new IllegalArgumentException("La date d'ouverture des ventes doit être antérieure à la date de fermeture.");
        }

        // 3. On ne vend pas des billets après le début de l'événement
        if (salesClosingDate.isAfter(startDate)) {
            throw new IllegalArgumentException("La fermeture des ventes doit être antérieure au début de l'événement.");
        }
    }

    /**
     * Vérifie si l'événement peut être publié.
     */
    public void publish() {
        if (this.categories == null || this.categories.isEmpty()) {
            throw new IllegalArgumentException("Impossible de publier un événement sans catégories de billets.");
        }
        if (LocalDateTime.now().isAfter(this.salesClosingDate)) {
            throw new IllegalArgumentException("Impossible de publier un événement dont les ventes sont déjà terminées.");
        }
        this.status = EventStatus.PUBLISHED;
    }
}