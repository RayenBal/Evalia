package com.example.evaliabackoffice.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_announcement")
public class Announce {
//    @Id
//    @GeneratedValue
//    private String idAnnouncement;
//    private String AnnounceName;
//    private String content;
//    private String image;
////    private Long verifiedById;
////    private LocalDateTime verifiedAt;
////    private String deliveryAddress;
////    private String estimatedDeliveryDate;
////    private String officeAddress;
////    private String timeSlots;
//    @ElementCollection(targetClass = TestMode.class)
//    @CollectionTable(name = "announcement_test_modes", joinColumns = @JoinColumn(name = "announcement_id"))
//    @Enumerated(EnumType.STRING)
//    @Column(name = "test_mode")
//    private List<TestMode> testModes = new ArrayList<>();
//
//    @ElementCollection
//    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "announcement_id"))
//    @Column(name = "image_url")
//    private List<String> productImages = new ArrayList<>();
//
////    private Integer productSize;
////    private String productColor;
//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "user_id",nullable = true)
//    private User user;
//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "admin_id",nullable = true)
//    private Admin admin;
//
//    @JsonIgnoreProperties({"announcements"})
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "Announcements_campagnes",
//            joinColumns = @JoinColumn(name = "announcement_id"),
//            inverseJoinColumns = @JoinColumn(name = "campagne_id")
//    )
//    private List<Campagne> campagnes = new ArrayList<>();
////    @JsonIgnoreProperties({"announcements"})
////    @ManyToMany(fetch = FetchType.LAZY)
////    @JoinTable(
////            name = "Announcements_quiz",
////            joinColumns = @JoinColumn(name = "announcement_id"),
////            inverseJoinColumns = @JoinColumn(name = "quiz_id")
////    )
////    private List<Quiz> quizList = new ArrayList<>();
////    @JsonIgnoreProperties({"announcements"})
////    @ManyToMany(fetch = FetchType.LAZY)
////    @JoinTable(
////            name = "Announcements_recompenses",
////            joinColumns = @JoinColumn(name = "announcement_id"),
////            inverseJoinColumns = @JoinColumn(name = "recompense_id")
////    )
////        private List<Recompenses> recompensesList = new ArrayList<>();
//
//
@Id
@UuidGenerator
private String idAnnouncement;
    private String announceName;
    private String content;
    private String image;
    private String productImages;
    @CreationTimestamp
    private LocalDateTime createdAt;
    //    private Long verifiedById;
//    private LocalDateTime verifiedAt;
    private String deliveryAddress;
//    private String estimatedDeliveryDate;
    private String officeAddress;
//    private String timeSlots;
    @ElementCollection(targetClass = TestMode.class)
    @CollectionTable(name = "announcement_test_modes", joinColumns = @JoinColumn(name = "announcement_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "test_mode")
    private List<TestMode> testModes = new ArrayList<>();

//    @ElementCollection
//    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "announcement_id"))
//    @Column(name = "image_url")
//    private List<String> productImages = new ArrayList<>();

    //    private Integer productSize;
//    private String productColor;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = true)
    private User user;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "admin_id",nullable = true)
    private Admin admin;

    @JsonIgnoreProperties({"announcements"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Announcements_campagnes",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "campagne_id")
    )
    private List<Campagne> campagnes = new ArrayList<>();



}
