package com.example.evaliabackoffice.entity;

public enum UserStatus {
    PENDING,      // inscrit, mail non vérifié ou en attente admin
    APPROVED,     // validé par l’admin
    REJECTED,     // rejeté par l’admin
    DISABLED      // autre désactivation manuelle
}
