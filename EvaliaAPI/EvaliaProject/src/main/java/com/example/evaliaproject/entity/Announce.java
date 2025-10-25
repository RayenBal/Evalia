package com.example.evaliaproject.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(onlyExplicitlyIncluded = true) // << remplace le toString généré par @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="_announcement")
public class Announce {
    @Id
    @UuidGenerator
    @EqualsAndHashCode.Include
    @ToString.Include
    private String idAnnouncement;

    @ToString.Include private String announceName;
    @ToString.Include private String content;

    private String image;
    private String productImages;

    @CreationTimestamp
    @ToString.Include
    private LocalDateTime createdAt;

    private String deliveryAddress;
    private String officeAddress;

    @ElementCollection(targetClass = TestMode.class)
    @CollectionTable(name = "announcement_test_modes", joinColumns = @JoinColumn(name = "announcement_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "test_mode")
    @ToString.Exclude
    private List<TestMode> testModes = new ArrayList<>();

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "user_id",nullable = true)
    @ToString.Exclude
    private User user;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "admin_id",nullable = true)
    @ToString.Exclude
    private Admin admin;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Announcements_campagnes",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "campagne_id"))
    @JsonIgnoreProperties({"announcements"})
    @ToString.Exclude
    private List<Campagne> campagnes = new ArrayList<>();

    @OneToMany(mappedBy = "announcement", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Quiz> quizList  = new ArrayList<>();

    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"announcement"})
    @ToString.Exclude
    private List<Recompenses> recompensesList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"announces","hibernateLazyInitializer","handler"})
    @ToString.Exclude
    private Category category;

    @OneToMany(mappedBy = "announcement",  cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Planning> plannings  = new ArrayList<>();
}
