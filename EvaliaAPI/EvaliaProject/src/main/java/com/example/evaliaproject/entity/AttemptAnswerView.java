package com.example.evaliaproject.entity;
//
//public record AttemptAnswerView(String questionId,
//                                String questionContent,
//                                String selectedResponseId,
//                                String selectedResponseContent,
//                                String freeText) {
//}
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttemptAnswerView {
    String questionId;
    String questionContent;

    String selectedResponseId;
    String selectedResponseContent;

    String freeText;

    // ⚠️ Méthode statique utilisée par ::from
    public static AttemptAnswerView from(AttemptAnswer a) {
        return AttemptAnswerView.builder()
                .questionId(a.getQuestion().getIdQuestion())
                .questionContent(a.getQuestion().getContent())
                .selectedResponseId(a.getSelectedResponse().getIdResponsePaneliste())
                .selectedResponseContent(a.getSelectedResponse().getContent())
                .freeText(a.getFreeText())
                .build();
    }
}