package com.ex.eventpro_api.reservation.internal.entity;

import com.ex.eventpro_api.core.exception.TicketAlreadyUsedException;
import com.ex.eventpro_api.reservation.internal.enums.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tickets",
        indexes = {
                @Index(name = "idx_ticket_code", columnList = "code"),
                @Index(name = "idx_ticket_reservation_line", columnList = "reservation_line_id")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le code unique (ex: UUID) qui sera scanné à l'entrée
    @NotBlank(message = "Le code du billet est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    // La ligne de réservation parente
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_line_id", nullable = false)
    private ReservationLine reservationLine;

    // Date d'émission du billet
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime issueDate;

    // Date du scan (null tant que le billet n'est pas scanné)
    private LocalDateTime scanDate;

    // ❌ Pas de Setter ! On utilise les méthodes métier pour changer le statut
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;


    // ==========================================
    // LOGIQUE MÉTIER (Domain Driven Design)
    // ==========================================

    /**
     * Vérifie si le billet est valide pour l'entrée.
     */
    public boolean validate() {
        return this.status == TicketStatus.VALID;
    }

    /**
     * Scanne le billet à l'entrée (Contrôle d'accès).
     * Passe le billet en USED et enregistre la date du scan.
     */
    public void scan() {
        if (this.status == TicketStatus.USED) {
            throw new TicketAlreadyUsedException("Ce billet a déjà été scanné (409 Conflict).");
        }
        if (this.status == TicketStatus.CANCELLED) {
            throw new TicketAlreadyUsedException("Impossible de scanner un billet annulé.");
        }

        this.status = TicketStatus.USED;
        this.scanDate = LocalDateTime.now();
    }

    /**
     * Annule le billet (ex: suite à un remboursement).
     */
    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }
}