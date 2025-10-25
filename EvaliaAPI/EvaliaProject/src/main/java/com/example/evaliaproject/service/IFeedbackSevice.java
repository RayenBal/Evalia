package com.example.evaliaproject.service;




import com.example.evaliaproject.dto.FeedbackStatsDto;
import com.example.evaliaproject.dto.OwnerFeedbackItemDto;
import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Feedback;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public interface IFeedbackSevice {

    Feedback createFeedback(String announcementId, Long panelistId, String comment, Integer rating);

    public List<OwnerFeedbackItemDto> listForOwner(String announcementId, String ownerEmail);
    public Announce ensureOwnership(String annId, String ownerEmail);
    Optional<Feedback> findMine(String announcementId, Long panelistId);


    /** Met à jour le feedback du paneliste pour l’annonce (rating/comment) */
    //Feedback updateMyFeedback(String announcementId, Long panelistId, Integer rating, String comment);

    /** Liste brute des feedbacks d’une annonce (entités) */
    List<Feedback> listByAnnouncement(String announcementId);

    /** Liste “owner” (DTO) protégée par ownership */


    /** Statistiques d’une annonce */
    FeedbackStatsDto statsForAnnouncement(String announcementId);

//    public Feedback addFeedback(Feedback feedback);
//
//
//    void deleteFeedback(Long idFeedback);
//
//    public List<Feedback> getAllFeedbacks();
//
//
//    Feedback DetailsFeedback(Long idFeedback);
//
//    Feedback updateFeedback(Feedback feedback, Long id);
//
}
