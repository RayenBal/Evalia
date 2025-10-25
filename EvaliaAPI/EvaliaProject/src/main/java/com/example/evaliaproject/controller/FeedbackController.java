package com.example.evaliaproject.controller;
import com.example.evaliaproject.dto.*;
import com.example.evaliaproject.entity.Feedback;
import com.example.evaliaproject.entity.TypeUser;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.FeedbackRepository;
import com.example.evaliaproject.repository.UserRepository;
import com.example.evaliaproject.service.FeedbackService;
import com.example.evaliaproject.service.IFeedbackSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    @Autowired
    private IFeedbackSevice feedbackService;

    @Autowired
    private  FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;



    /** ✅ Version simple : rating + comment (multipart sans fichier) */
    @PostMapping(
            value = "/simple/announces/{announcementId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Feedback createSimple(
            @PathVariable String announcementId,
            //  @RequestParam("panelistId") Long panelistId,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "comment", required = false) String comment,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Non authentifié");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));

        if (user.getTypeUser() != TypeUser.Paneliste) {
            throw new org.springframework.security.access.AccessDeniedException("Seuls les panelistes peuvent laisser un feedback.");
        }

        return feedbackService.createFeedback(announcementId, user.getId_user(), comment, rating);
    }

    // optionnel : listings
    @GetMapping("/announces/{announcementId}")
    public List<Feedback> byAnnouncement(@PathVariable String announcementId) {
        return feedbackRepository.findByAnnouncement_IdAnnouncement(announcementId);
    }

    @GetMapping("/mine/announces/{announcementId}")
    public ResponseEntity<Feedback> myFeedback(
            @PathVariable String announcementId,
            Authentication authentication
    ) {
        // var email = authentication.getName();
        //var panelist = userRepository.findByEmail(email)
        //   .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
        //var opt = feedbackRepository.findOneByAnnouncementAndPanelist(announcementId, panelist.getId_user());
        // return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
        String email = authentication.getName();
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
        return feedbackRepository.findMine(announcementId, me.getId_user())
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    // ✅ Modifier MON feedback explicitement (PUT JSON)
    public record FeedbackUpdate(Integer rating, String comment) {}
    @PutMapping(value="/mine/announces/{announcementId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Feedback updateMine(
            @PathVariable String announcementId,
            @RequestBody FeedbackUpdate body,
            Authentication authentication
    ) {
        var email = authentication.getName();
        var panelist = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
        return ((FeedbackService)feedbackService).createFeedback(
                announcementId, panelist.getId_user(), body.comment(), body.rating()
        );
    }
//    @GetMapping("/panelists/{panelistId}")
//    public List<Feedback> byPanelist(@PathVariable Long panelistId) {
//        return feedbackRepository.findByPanelist_Id(panelistId);
//    }


    /** ----------------- FEEDBACK du paneliste COURANT (“mine”) ----------------- */
//    @GetMapping("/mine/announces/{announcementId}")
//    public ResponseEntity<Feedback> myFeedback(
//            @PathVariable String announcementId,
//            Authentication authentication
//    ) {
//        String email = authentication.getName();
//        User me = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
//
//        return feedbackRepository.findMine(announcementId, me.getId_user())
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    /** ----------------- STATS pour une annonce ----------------- */
    @GetMapping("/announces/{announcementId}/stats")
    public FeedbackStatsDto stats(@PathVariable String announcementId) {
        return feedbackService.statsForAnnouncement(announcementId);

    }

//    /** ----------------- LISTE OWNER (DTO) ----------------- */
//    @GetMapping("/owner/announces/{announcementId}")
//    public List<OwnerFeedbackItemDto> ownerList(
//            @PathVariable String announcementId,
//            Authentication authentication
//    ) {
//        String ownerEmail = authentication.getName();
//        return feedbackService.listForOwner(announcementId, ownerEmail);
//    }
//
//    /** ----------------- MAJ de mon feedback (JSON simple) ----------------- */
//    public static class MyFeedbackUpdateRequest {
//        public Integer rating;
//        public String comment;
//    }

//    @PutMapping("/mine/announces/{announcementId}")
//    public ResponseEntity<?> updateMine(
//            @PathVariable String announcementId,
//            @RequestBody MyFeedbackUpdateRequest body,
//            Authentication authentication
//    ) {
//        String email = authentication.getName();
//        User me = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
//
//        Feedback f = feedbackRepository.findMine(announcementId, me.getId_user())
//                .orElseThrow(() -> new IllegalArgumentException("Aucun feedback à mettre à jour pour cette annonce."));
//
//        f.setRating(body.rating);
//        f.setComment(body.comment);
//        feedbackRepository.save(f);
//        return ResponseEntity.ok().build();
//    }



    @GetMapping("/me")
    public List<MyFeedbackItemDto> myFeedbacks(Authentication authentication) {
        String email = authentication.getName();
        var me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
        return ((FeedbackService) feedbackService).listMine(me.getId_user());
    }

    @GetMapping("/me/rewards")
    public PanelistRewardsDto myRewards(Authentication authentication) {
        String email = authentication.getName();
        var me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
        //   return ((FeedbackService) feedbackService).rewardsFor(me.getId_user());
        var dto = ((FeedbackService) feedbackService).rewardsFor(me.getId_user());
        System.out.println("DEBUG /me/rewards -> items=" + dto.items().size() + ", totals=" + dto.totals());
        return dto;
    }




    @GetMapping("/owner/rewards")
    public List<OwnerRewardItemDto> ownerRewards(Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return ((FeedbackService) feedbackService).ownerRewards(auth.getName());
    }

    @GetMapping("/owner/announces/{announcementId}/rewards")
    public List<OwnerRewardItemDto> ownerRewardsForAnnounce(@PathVariable String announcementId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return ((FeedbackService) feedbackService).ownerRewardsForAnnounce(auth.getName(), announcementId);
    }

    // --- MAJ statut ---
    public record UpdateStatusReq(String status) {}

    @PutMapping("/owner/rewards/{earnedRewardId}/status")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void updateRewardStatus(@PathVariable String earnedRewardId,
                                   @RequestBody UpdateStatusReq body,
                                   Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        com.example.evaliaproject.entity.RewardPayoutStatus newStatus =
                com.example.evaliaproject.entity.RewardPayoutStatus.valueOf(body.status());
        ((FeedbackService) feedbackService).updateRewardStatus(auth.getName(), earnedRewardId, newStatus);
    }
    @GetMapping({"/owner/annonces/{announcementId}", "/owner/announces/{announcementId}"})
    public List<OwnerFeedbackItemDto> ownerList(
            @PathVariable String announcementId,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED
            );
        }
        String ownerEmail = authentication.getName();
        return feedbackService.listForOwner(announcementId, ownerEmail);
    }
}