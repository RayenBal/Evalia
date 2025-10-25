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
@Table(name="_question")
public class Question {
    @Id
    @UuidGenerator
    private String idQuestion;
    private String content;
//    @ManyToMany(mappedBy = "questions")
//    @JsonIgnore
//    private List<Quiz> quizList = new ArrayList<>();

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "Question_responsePaneliste",
//            joinColumns = @JoinColumn(name = "question_id"),
//            inverseJoinColumns = @JoinColumn(name = "responsePaneliste_id")
//    )
//    private List<ResponsePaneliste> responsePanelisteList = new ArrayList<>();
//@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//@JsonManagedReference
@OneToMany(mappedBy="question", cascade=ALL, orphanRemoval=true, fetch=LAZY)
@Fetch(FetchMode.SUBSELECT)
@JsonManagedReference
private List<ResponsePaneliste> responses;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonBackReference
    private Quiz quiz;
}
