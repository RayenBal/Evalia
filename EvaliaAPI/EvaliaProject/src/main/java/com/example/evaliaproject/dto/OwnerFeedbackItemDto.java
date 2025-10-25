package com.example.evaliaproject.dto;
import java.time.LocalDateTime;

public class OwnerFeedbackItemDto {
    private String idFeedback;
    private LocalDateTime createdAt;
    private Integer rating;
    private String comment;

    private Long panelistId;
    private String panelistFirstname;
    private String panelistLastname;

    public OwnerFeedbackItemDto() {}

    public OwnerFeedbackItemDto(String idFeedback, LocalDateTime createdAt, Integer rating, String comment,
                                Long panelistId, String panelistFirstname, String panelistLastname) {
        this.idFeedback = idFeedback;
        this.createdAt = createdAt;
        this.rating = rating;
        this.comment = comment;
        this.panelistId = panelistId;
        this.panelistFirstname = panelistFirstname;
        this.panelistLastname = panelistLastname;
    }

    public String getIdFeedback() { return idFeedback; }
    public void setIdFeedback(String idFeedback) { this.idFeedback = idFeedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getPanelistId() { return panelistId; }
    public void setPanelistId(Long panelistId) { this.panelistId = panelistId; }
    public String getPanelistFirstname() { return panelistFirstname; }
    public void setPanelistFirstname(String panelistFirstname) { this.panelistFirstname = panelistFirstname; }
    public String getPanelistLastname() { return panelistLastname; }
    public void setPanelistLastname(String panelistLastname) { this.panelistLastname = panelistLastname; }
}