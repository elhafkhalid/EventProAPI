package com.ex.eventpro_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "administrators")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Administrator extends User {
    // Pas d'attributs spécifiques pour le moment
}