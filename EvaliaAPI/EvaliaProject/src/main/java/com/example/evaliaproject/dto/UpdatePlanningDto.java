package com.example.evaliaproject.dto;

import com.example.evaliaproject.entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdatePlanningDto {
    private String announcementId;     // changer d’annonce (owner only)
    private Long panelistId;           // assigner/unassign (null ou <=0 => retirer)
    private LocalDateTime startsAt;    // changer début
    private LocalDateTime endsAt;      // changer fin
    private AppointmentStatus status;
}