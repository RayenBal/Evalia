package com.example.evaliaproject.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Table(name="_feedback")
@Table(
        name = "_feedback",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_feedback_annonce_paneliste",
                columnNames = {"announcement_id", "panelist_id"}
        )
)
public class Feedback {
    @Id
    @UuidGenerator
    private String idFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    @JsonIgnoreProperties({
            "quizList","recompensesList","campagnes","user","admin",
            "hibernateLazyInitializer","handler"
    })
    private Announce announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panelist_id", nullable = false)
    @JsonIgnoreProperties({"password","roles","hibernateLazyInitializer","handler"})

    private User panelist;

    /**
     * Données du formulaire générique, stockées en JSON string.
     * Exemple: { "Q1": "Oui", "Q2": "Non", "note": "5" }
     */
//    @Lob
//    @Column(name = "form_data_json", columnDefinition = "TEXT")
//    private String formDataJson;

    /** Commentaire libre du testeur */
    @Column(length = 4000)
    private String comment;

    /** Rating 1–5 (optionnel) */
    private Integer rating;

    /** Nom du fichier image uploadée */
    //private String imageName;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
