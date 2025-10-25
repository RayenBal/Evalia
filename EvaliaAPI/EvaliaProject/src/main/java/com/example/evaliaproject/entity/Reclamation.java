package com.example.evaliaproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_reclamation")
@EntityListeners(AuditingEntityListener.class)   // <-- ajoute ceci

public class Reclamation {
    @Id
    @UuidGenerator
    private String Idreclamation;
    private String content;
//    @Enumerated
//    typeReclamation typeReclamation;
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Motif motif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_user", nullable = false)          // <— le nom doit être EXACTEMENT type_user
    private TypeUser userType;
    @PrePersist @PreUpdate
    private void syncUserType() {
        if (user != null) userType = user.getTypeUser();
    }


    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
}
