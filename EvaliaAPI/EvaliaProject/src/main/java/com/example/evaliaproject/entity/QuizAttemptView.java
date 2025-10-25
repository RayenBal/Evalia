package com.example.evaliaproject.entity;

import java.time.LocalDateTime;
import java.util.List;
//
//public record QuizAttemptView(String idAttempt,
//                              String quizId,
//                              String quizContent,
//                              String panelistEmail,
//                              String panelistName,
//                              LocalDateTime startedAt,
//                              LocalDateTime submittedAt,
//                              AttemptStatus status,
//                              List<AttemptAnswerView> answers) {
//}
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class QuizAttemptView {
    String idAttempt;
    LocalDateTime submittedAt;
    String status;               // "SUBMITTED" | "STARTED"
    String panelistEmail;        // email du paneliste
    String quizContent;
    private String deliveryAddress;
    private String numTelephone;

    // titre/texte du quiz
    List<AttemptAnswerView> answers;

    public static QuizAttemptView from(QuizAttempt qa) {
        return QuizAttemptView.builder()
                .idAttempt(qa.getIdAttempt())
                .submittedAt(qa.getSubmittedAt())
                .status(qa.getStatus().name())
                .panelistEmail(qa.getPanelist().getEmail())
                .deliveryAddress(qa.getPanelist().getDeliveryAddress())
                .numTelephone(qa.getPanelist().getNumTelephone())
                .quizContent(qa.getQuiz().getContent())
                .answers(
                        qa.getAnswers().stream()
                                .map(AttemptAnswerView::from)
                                .toList()
                )
                .build();
    }
}