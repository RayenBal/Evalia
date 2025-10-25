package com.example.evaliaproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_quiz_attempt")
public class QuizAttempt {
    @Id @UuidGenerator
    private String idAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    @JsonIgnoreProperties({"quizList","recompensesList","campagnes","user","admin"})
    private Announce announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnoreProperties({"announcement","questions"})
    private Quiz quiz;

    // Ton entité User existante (utilisée comme paneliste)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panelist_id", nullable = false)
    @JsonIgnoreProperties({"password","roles"})
    private User panelist;

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    // optionnel si tu veux corriger/évaluer
    private Integer score;

    // simple flag : une récompense a été attribuée
    private boolean rewardGranted;

    @OneToMany(mappedBy="attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"attempt"})
    private List<AttemptAnswer> answers = new ArrayList<>();
}
