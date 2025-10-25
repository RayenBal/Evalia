package com.example.evaliaproject.dto;
import com.example.evaliaproject.entity.typeRecompenses;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public record PanelistRewardsDto(
        List<PanelistRewardItemDto> items,
        Map<typeRecompenses, BigDecimal> totals
) {}