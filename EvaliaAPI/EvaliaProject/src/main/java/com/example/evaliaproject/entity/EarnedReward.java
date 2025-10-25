package com.example.evaliaproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(
        name = "_earned_reward",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_earnedreward_ann_panelist_rewardtype",
                columnNames = {"announcement_id", "panelist_id", "reward_type"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EarnedReward {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "announcement_id", nullable = false)
    private Announce announcement;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "panelist_id", nullable = false)
    private User panelist;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private typeRecompenses rewardType;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "label")
    private String label; // optionnel (code bon d'achat, etc.)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RewardPayoutStatus status = RewardPayoutStatus.PAS_ENCORE;
}
