package com.example.evaliaproject.dto;

import com.example.evaliaproject.entity.RewardPayoutStatus;
import com.example.evaliaproject.entity.typeRecompenses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OwnerRewardItemDto(
        String earnedRewardId,
        String announcementId,
        String announcementName,
        Long panelistId,
        String panelistFirstname,
        String panelistLastname,
        String panelistEmail,
        String panelistIban,
        typeRecompenses rewardType,
        BigDecimal amount,
        RewardPayoutStatus status,
        LocalDateTime createdAt
) {}