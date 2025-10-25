package com.example.evaliaproject.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_quiz")
public class Quiz {
    @Id
    @UuidGenerator
    private String idQuiz;
    private String content;
//    @ManyToMany(mappedBy = "quizList")
//    @JsonIgnore
//    private List<Announce> announcements = new ArrayList<>();

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "quiz_questions",
//            joinColumns = @JoinColumn(name = "quiz_id"),
//            inverseJoinColumns = @JoinColumn(name = "question_id")
//    )
//    private List<Question> questions = new ArrayList<>();

@ManyToOne
@JoinColumn(name = "announcement_id") // Ce champ sera créé en base dans la table quiz
@JsonBackReference
private Announce announcement;
//    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
    @OneToMany(mappedBy="quiz", cascade=ALL, orphanRemoval=true, fetch=LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<Question> questions;
}
