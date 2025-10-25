package com.example.evaliaproject.dto;


import com.example.evaliaproject.entity.AppointmentStatus;
import com.example.evaliaproject.entity.Planning;

import java.time.LocalDateTime;

public record PlanningDto(
        String id,
        String announcementId,
        Long ownerId,
        Long panelistId,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        AppointmentStatus status
) {
    public   static PlanningDto of(Planning p) {
        return new PlanningDto(
                p.getId(),
                p.getAnnouncement()!=null ? p.getAnnouncement().getIdAnnouncement() : null,
                p.getOwner()!=null ? p.getOwner().getId_user() : null,
                p.getPanelist()!=null ? p.getPanelist().getId_user() : null,
                p.getStartsAt(),
                p.getEndsAt(),
                p.getStatus()
        );
    }
}