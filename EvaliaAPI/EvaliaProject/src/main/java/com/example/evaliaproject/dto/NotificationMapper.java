package com.example.evaliaproject.dto;

import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Notification;

public final class NotificationMapper {
    private NotificationMapper(){}

    public static NotificationDto toDto(Notification n) {
        Announce a = n.getAnnouncement();
        return new NotificationDto(
                n.getIdnotif(),
                n.getMessage(),
                n.getType(),
                n.isSeen(),
                n.getCreatedAt(),
                a != null ? a.getIdAnnouncement() : null,
                a != null ? a.getAnnounceName()  : null
        );
    }
}
