package com.example.evaliaproject.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SlotCreateDto {
    private String announcementId;
    private List<Slot> slots;

    @Data
    public static class Slot {
        private LocalDateTime startsAt;
        private LocalDateTime endsAt;
        private Long panelistId; // ← optionnel

    }
}