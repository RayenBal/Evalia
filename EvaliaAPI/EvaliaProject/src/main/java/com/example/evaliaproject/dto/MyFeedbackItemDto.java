package com.example.evaliaproject.dto;
import java.time.LocalDateTime;

public class MyFeedbackItemDto {
    private String announcementId;
    private String announcementName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public MyFeedbackItemDto() {}
    public MyFeedbackItemDto(String announcementId, String announcementName,
                             Integer rating, String comment, LocalDateTime createdAt) {
        this.announcementId = announcementId;
        this.announcementName = announcementName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
