package com.example.evaliaproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "_attempt_answer")
public class AttemptAnswer {
    @Id @UuidGenerator
    private String idAttemptAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonIgnoreProperties({"answers"})
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"quiz","responses"})
    private Question question;

    // Le choix sélectionné (parmi Question.responses)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_response_id", nullable = false)
    @JsonIgnoreProperties({"question"})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ResponsePaneliste selectedResponse;

    // Optionnel : si tu veux accepter un texte libre
    private String freeText;
}
