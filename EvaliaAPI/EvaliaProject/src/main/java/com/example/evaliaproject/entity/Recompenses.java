package com.example.evaliaproject.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_recompenses")
public class Recompenses {
    @Id
    @UuidGenerator
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_recompense")
    private String IdRecompense;
    @Enumerated(EnumType.STRING)
    @Column(name = "type_recompenses", nullable = false)
    typeRecompenses typeRecompenses;

    // Optionnel mais recommandé: un "montant/valeur" générique
    // - BonsDachats / Argent -> BigDecimal
    // - Points -> entier (on peut réutiliser amount en tant que nombre de points)
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;
    @Column
    private String label; // ex: "Bon d'achat Carrefour 50 TND", "500 points", etc.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id_announcement", nullable = false) // ⚠️ même nom que dans la DB
    @JsonIgnore
    private Announce announcement;
}
