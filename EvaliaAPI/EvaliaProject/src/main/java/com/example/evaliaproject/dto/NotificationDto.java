package com.example.evaliaproject.dto;


import com.example.evaliaproject.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationDto(
        String id,
        String message,
        NotificationType type,
        boolean seen,
        LocalDateTime createdAt,
        String announcementId,
        String announcementName
) {}