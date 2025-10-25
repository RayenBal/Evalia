package com.example.evaliaproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategory", nullable = false)
    private Long idcategory;
    @Column(name = "name_category")
    private String nameCategory;
    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Announce> announces = new ArrayList<>();
}
