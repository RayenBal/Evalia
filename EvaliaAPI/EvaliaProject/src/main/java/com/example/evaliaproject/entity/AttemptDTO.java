package com.example.evaliaproject.entity;


import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AttemptDTO {
    private String idAttempt;
    private AttemptStatus status;

    public static AttemptDTO from(QuizAttempt a) {
        return AttemptDTO.builder()
                .idAttempt(a.getIdAttempt())
                .status(a.getStatus())
                .build();
    }
}