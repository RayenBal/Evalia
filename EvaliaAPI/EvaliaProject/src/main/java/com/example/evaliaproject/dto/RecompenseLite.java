package com.example.evaliaproject.dto;


import com.example.evaliaproject.entity.Recompenses;
import com.example.evaliaproject.entity.typeRecompenses;

import java.math.BigDecimal;

public record RecompenseLite(
        typeRecompenses typeRecompenses,
            BigDecimal amount,
            String label
    ) {
        public static RecompenseLite from(Recompenses r) {
            return new RecompenseLite(r.getTypeRecompenses(), r.getAmount(), r.getLabel());
        }
    }
