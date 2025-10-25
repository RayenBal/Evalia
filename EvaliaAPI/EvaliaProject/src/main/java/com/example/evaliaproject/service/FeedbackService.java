package com.example.evaliaproject.service;
import com.example.evaliaproject.dto.*;
import com.example.evaliaproject.entity.*;

import java.math.BigDecimal;
import java.util.*;

import com.example.evaliaproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.example.evaliaproject.entity.Feedback;
import com.example.evaliaproject.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;
import java.nio.file.*;

@Service
@RequiredArgsConstructor

public class FeedbackService implements IFeedbackSevice{
    @Autowired
    private  FeedbackRepository feedbackRepository;
    @Autowired
    private  AnnouncementRepository announcementRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired private EarnedRewardRepository earnedRewardRepository;
    @Autowired
    private NotificationService notificationService;

@Transactional
public Feedback createFeedback(
        String announcementId,
        Long panelistId,
        String comment,
        Integer rating
) {
    if (rating != null && (rating < 1 || rating > 5)) {
        throw new IllegalArgumentException("Le rating doit être entre 1 et 5.");
    }

    User panelist = userRepository.findById(panelistId)
            .orElseThrow(() -> new IllegalArgumentException("Paneliste introuvable: " + panelistId));

    Announce announce = announcementRepository.findById(announcementId)
            .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable: " + announcementId));

    var existing = feedbackRepository.findOneByAnnouncementAndPanelist(announcementId, panelistId);
    if (existing.isPresent()) {
        // 🔁 MAJ simple, pas de re-crédit
        var f = existing.get();
        f.setComment(comment);
        f.setRating(rating);
        return feedbackRepository.save(f);
    }

    // ✅ NOUVEAU feedback
    Feedback saved = feedbackRepository.save(
            Feedback.builder()
                    .announcement(announce)
                    .panelist(panelist)
                    .comment(comment)
                    .rating(rating)
                    .build()
    );

    // 💳 Créditer les récompenses une seule fois
    if (announce.getRecompensesList() != null && !announce.getRecompensesList().isEmpty()) {
        for (var r : announce.getRecompensesList()) {
            // Sur-sécurité pour éviter doublons si contrainte non respectée ailleurs
            boolean already = earnedRewardRepository
                    .existsForPanelistInAnnouncement(
                            announce.getIdAnnouncement(), panelistId, r.getTypeRecompenses());
            if (!already) {
                var savedEr =  earnedRewardRepository.save(
                        EarnedReward.builder()
                                .announcement(announce)
                                .panelist(panelist)
                                .rewardType(r.getTypeRecompenses())
                                .amount(r.getAmount() == null ? java.math.BigDecimal.ZERO : r.getAmount())
                                .label(r.getLabel())
                                .status(RewardPayoutStatus.PAS_ENCORE) // ✅
                                .build()
                );
                System.out.println("✅ EarnedReward créé: " + savedEr.getId());

            }
        }
    }
// 📨 Notifications (messages demandés)
    String annName = announce.getAnnounceName();

// Annonceur (propriétaire de l'annonce)
    if (announce.getUser() != null) {
        String ownerMsg = "Le paneliste " + panelist.fullName()
                + " a laissé un feedback de l'annonce \"" + annName + "\""
                + (announce.getRecompensesList() != null && !announce.getRecompensesList().isEmpty()
                ? " et gagne les récompenses de cette annonce."
                : ".");
        notificationService.notify(announce.getUser(), announce, ownerMsg, NotificationType.FEEDBACK_RECEIVED);
    }

// Paneliste
    String panelistMsg = "Tu as laissé un feedback de l'annonce \"" + annName + "\""
            + (announce.getRecompensesList() != null && !announce.getRecompensesList().isEmpty()
            ? " et gagnes des récompenses de cette annonce."
            : ".");
    notificationService.notify(panelist, announce, panelistMsg, NotificationType.REWARD_GAINED);
 return saved;


}


    private String formatRewards(com.example.evaliaproject.entity.Announce announce) {
        if (announce == null || announce.getRecompensesList() == null || announce.getRecompensesList().isEmpty()) {
            return "";
        }
        return announce.getRecompensesList().stream()
                .map(r -> {
                    var amount = r.getAmount() == null ? java.math.BigDecimal.ZERO : r.getAmount();
                    var type   = r.getTypeRecompenses() == null ? "" : r.getTypeRecompenses().name();
                    var label  = (r.getLabel() != null && !r.getLabel().isBlank()) ? " (" + r.getLabel() + ")" : "";
                    return amount + " " + type + label;
                })
                .collect(java.util.stream.Collectors.joining(", "));
    }
    @Override
    @Transactional(readOnly = true)
    public List<OwnerFeedbackItemDto> listForOwner(String announcementId, String ownerEmail) {
        ensureOwnership(announcementId, ownerEmail);
        return feedbackRepository.ownerList(announcementId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Feedback> findMine(String announcementId, Long panelistId) {
        return feedbackRepository.findMine(announcementId, panelistId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Feedback> listByAnnouncement(String announcementId) {
        return feedbackRepository.findByAnnouncement_IdAnnouncement(announcementId);
    }

    public Announce ensureOwnership(String annId, String ownerEmail) {
        Announce a = announcementRepository.findById(annId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable: " + annId));
        if (a.getUser() == null || a.getUser().getEmail() == null ||
                !a.getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new AccessDeniedException("Vous n’êtes pas propriétaire de cette annonce.");
        }
        return a;
    }



    @Override
    @Transactional(readOnly = true)
    public FeedbackStatsDto statsForAnnouncement(String announcementId) {
        long total = feedbackRepository.countByAnnouncement_IdAnnouncement(announcementId);
        Double avg = feedbackRepository.averageRating(announcementId);
        long s1 = feedbackRepository.countByAnnouncement_IdAnnouncementAndRating(announcementId, 1);
        long s2 = feedbackRepository.countByAnnouncement_IdAnnouncementAndRating(announcementId, 2);
        long s3 = feedbackRepository.countByAnnouncement_IdAnnouncementAndRating(announcementId, 3);
        long s4 = feedbackRepository.countByAnnouncement_IdAnnouncementAndRating(announcementId, 4);
        long s5 = feedbackRepository.countByAnnouncement_IdAnnouncementAndRating(announcementId, 5);

        return new FeedbackStatsDto(total, avg == null ? 0.0 : avg, s1, s2, s3, s4, s5);
    }
    @Transactional(readOnly = true)
    public List<MyFeedbackItemDto> listMine(Long panelistId) {
        return feedbackRepository.findAllMine(panelistId);
    }
    @Transactional(readOnly = true)
    public PanelistRewardsDto rewardsFor(Long panelistId) {
        var items = earnedRewardRepository.listForPanelist(panelistId);

        var dtos = items.stream().map(er ->
                new PanelistRewardItemDto(
                        er.getAnnouncement().getIdAnnouncement(),
                        er.getAnnouncement().getAnnounceName(),
                        er.getRewardType(),
                        er.getAmount(),
                        er.getStatus(),
                        er.getCreatedAt()
                )
        ).toList();

        var totals = new EnumMap<typeRecompenses, BigDecimal>(typeRecompenses.class);
        for (var er : items) {
            totals.merge(er.getRewardType(),
                    er.getAmount() == null ? BigDecimal.ZERO : er.getAmount(),
                    BigDecimal::add);
        }

        return new PanelistRewardsDto(dtos, totals);
    }


    @Transactional(readOnly = true)
    public List<OwnerRewardItemDto> ownerRewards(String ownerEmail) {
        var owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + ownerEmail));
        return earnedRewardRepository.ownerRewards(owner.getId_user());
    }

    @Transactional(readOnly = true)
    public List<OwnerRewardItemDto> ownerRewardsForAnnounce(String ownerEmail, String annId) {
        var owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + ownerEmail));
        return earnedRewardRepository.ownerRewardsForAnnounce(owner.getId_user(), annId);
    }

    @Transactional
    public void updateRewardStatus(String ownerEmail, String earnedRewardId, RewardPayoutStatus status) {
        var er = earnedRewardRepository.findByIdWithOwner(earnedRewardId)
                .orElseThrow(() -> new IllegalArgumentException("Reward introuvable: " + earnedRewardId));
        if (er.getAnnouncement().getUser() == null
                || !er.getAnnouncement().getUser().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("Pas propriétaire de cette annonce.");
        }
        er.setStatus(status);
        earnedRewardRepository.save(er);
    }

}