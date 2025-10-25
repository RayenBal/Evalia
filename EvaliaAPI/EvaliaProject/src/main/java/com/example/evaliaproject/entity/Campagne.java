package com.example.evaliaproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_campagne")
public class Campagne {
    @Id
    @GeneratedValue
    private Long idCampagne;
    private String name;
    private String description;
    private LocalDate startDate;

    private LocalDate endDate;
    @ManyToMany(mappedBy = "campagnes")
    @JsonIgnore
    private List<Announce> announcements = new ArrayList<>();



}
