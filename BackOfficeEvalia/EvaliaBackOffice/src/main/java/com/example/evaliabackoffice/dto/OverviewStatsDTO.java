package com.example.evaliabackoffice.dto;
import java.util.List;


public record OverviewStatsDTO(
        long total,
        List<MotifCount> byMotif,
        List<UserTypeCount> byType
) {}