package com.ex.eventpro_api.event.entity;

import com.ex.eventpro_api.promotion.entity.Promotion;
import com.ex.eventpro_api.event.enums.EventStatus;
import com.ex.eventpro_api.user.entity.Organizer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private String location;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;
    private LocalDateTime salesOpeningDate;
    private LocalDateTime salesClosingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    // Un événement a plusieurs configurations de catégories
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketCategoriesPerEvent> categories;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Promotion promotion;

    public boolean isOpenForSales() {
        LocalDateTime now = LocalDateTime.now();

        return this.status == EventStatus.PUBLISHED &&
                now.isAfter(this.salesOpeningDate) &&
                now.isBefore(this.salesClosingDate);
    }
}