package com.example.evaliaproject.dto;
import com.example.evaliaproject.entity.RewardPayoutStatus;
import com.example.evaliaproject.entity.typeRecompenses;
import java.math.BigDecimal;
import java.time.LocalDateTime;

    public class PanelistRewardItemDto {
        private String announcementId;
        private String announcementName;
        private typeRecompenses rewardType;
        private BigDecimal amount;
        private RewardPayoutStatus status;
        private LocalDateTime createdAt;


        public PanelistRewardItemDto() {}
        public PanelistRewardItemDto(String announcementId, String announcementName,
                                     typeRecompenses rewardType, BigDecimal amount,RewardPayoutStatus status, LocalDateTime createdAt) {
            this.announcementId = announcementId;
            this.announcementName = announcementName;
            this.rewardType = rewardType;
            this.amount = amount;
            this.status = status;
            this.createdAt = createdAt;

        }

        public String getAnnouncementId() { return announcementId; }
        public String getAnnouncementName() { return announcementName; }
        public typeRecompenses getRewardType() { return rewardType; }
        public BigDecimal getAmount() { return amount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public RewardPayoutStatus getStatus() { return status; }

    }