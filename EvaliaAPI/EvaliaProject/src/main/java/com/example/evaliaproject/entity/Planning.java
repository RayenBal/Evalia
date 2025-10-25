package com.example.evaliaproject.entity;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Un créneau "test au bureau".
 * Un seul paneliste par rendez-vous (ManyToOne).
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@ToString(onlyExplicitlyIncluded = true) // << ajouté
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // << ajouté
@Table(name = "_planning")
public class Planning {

    @Id
    @UuidGenerator
    @EqualsAndHashCode.Include // equals/hashCode basés sur l'id uniquement
    @ToString.Include
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    @ToString.Exclude // << coupe la récursion
    private Announce announcement;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panelist_id")
    @ToString.Exclude
    private User panelist;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @ToString.Include
    private LocalDateTime startsAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @ToString.Include
    private LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private AppointmentStatus status = AppointmentStatus.PENDING;
}