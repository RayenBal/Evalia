package com.example.evaliabackoffice.dto;


import java.time.LocalDate;

public interface DailyCount {
    LocalDate getDay();
    long getTotal();
}