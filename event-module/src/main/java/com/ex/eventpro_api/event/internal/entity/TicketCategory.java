package com.ex.eventpro_api.event.internal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ticket_categories",
        indexes = {
                // L'index sur "name" est déjà géré par @Column(unique = true),
                // mais on peut ajouter un index pour les recherches par nom.
                @Index(name = "idx_category_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 50, message = "Le nom de la catégorie ne doit pas dépasser 50 caractères")
    @Column(nullable = false, unique = true, length = 50)
    private String name; // ex: "VIP", "Standard"

    // --- AUDIT (Optionnel mais bonne pratique) ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Un petit constructeur pratique pour créer une catégorie à la volée
    public TicketCategory(String name) {
        this.name = name;
    }
}