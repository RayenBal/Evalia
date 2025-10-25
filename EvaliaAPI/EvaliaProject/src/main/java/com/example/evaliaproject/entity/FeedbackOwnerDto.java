package com.example.evaliaproject.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor
public class FeedbackOwnerDto {
    private String idFeedback;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long panelistId;
    private String panelistName;
    private String panelistEmail;
    private String announcementId;
}
