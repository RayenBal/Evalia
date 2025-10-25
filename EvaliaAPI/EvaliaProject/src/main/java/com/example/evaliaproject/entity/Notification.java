package com.example.evaliaproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "_notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)     // ✅ génère un UUID
    @Column(name = "id_notification")
    private String idnotif;



    /** Propriétaire de la notification (paneliste OU annonceur) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    /** Optionnel : rattacher à l’annonce concernée */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private Announce announcement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private boolean seen = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}