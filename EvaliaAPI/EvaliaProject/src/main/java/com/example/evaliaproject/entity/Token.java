package com.example.evaliaproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;
    @ManyToOne(optional = false)
    @JoinColumn(name="userId",nullable = false)
    private User user;
    @Column(nullable = false)
    private boolean revoked = false;
    @Column(name = "used_at")
    private LocalDateTime usedAt;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;



}
