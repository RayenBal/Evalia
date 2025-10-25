package com.example.evaliaproject.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_responseP")
public class ResponsePaneliste {
    @Id
    @UuidGenerator
    private String IdResponsePaneliste;
//    private Long IdPaneliste;

//    @ManyToMany(mappedBy = "responsePanelisteList")
//    @JsonIgnore
//    private List<Question> questions = new ArrayList<>();

    private String content; // Le texte de la réponse (ex: "Sèche", "Grasse", etc.)

//    private boolean isCorrect; // (optionnel) Vrai si c’est la bonne réponse (utile pour les quiz évaluatifs)

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private Question question;
}
