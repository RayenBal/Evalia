package com.example.evaliaproject.controller;

import com.example.evaliaproject.entity.*;
import com.example.evaliaproject.service.QuizFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;


@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/participation")
@RequiredArgsConstructor
public class ParticipantQuizController {
    private final QuizFlowService quizFlowService;

    /**
     * 1) Démarrer une tentative pour un paneliste sur un quiz d'une annonce.
     *
     * Exemple d’appel:
     * POST /participation/announces/{announceId}/quizzes/{quizId}/start?panelistId=42
//     */
//    @PostMapping("/announces/{announceId}/quizzes/{quizId}/start")
//    public QuizAttempt startAttempt(@PathVariable String announceId,
//                                    @PathVariable String quizId,
//                                    @RequestParam("panelistId") Long panelistId) {
//        return quizFlowService.startAttempt(announceId, quizId, panelistId);
//    }
//
//    /**
//     * 2) Soumettre les réponses (SANS DTO) pour une tentative.
//     *
//     * Body JSON attendu: List<AttemptAnswer>
//     * [
//     *   { "question": { "idQuestion": "Q1" }, "selectedResponse": { "IdResponsePaneliste": "R1" } },
//     *   { "question": { "idQuestion": "Q2" }, "selectedResponse": { "IdResponsePaneliste": "R5" }, "freeText": "..." }
//     * ]
//     *
//     * Exemple d’appel:
//     * POST /participation/attempts/{attemptId}/submit-raw
//     */
//    @PostMapping("/attempts/{attemptId}/submit-raw")
//    public QuizAttempt submitRaw(@PathVariable String attemptId,
//                                 @RequestBody List<AttemptAnswer> answers) {
//        return quizFlowService.submitRawAnswers(attemptId, answers);
//    }

//    @PostMapping("/announces/{announceId}/quizzes/{quizId}/start")
//    public QuizAttempt startAttempt(@PathVariable String announceId,
//                                    @PathVariable String quizId,
//                                    Authentication authentication) {
//        String email = authentication.getName();
//        return quizFlowService.startAttemptForCurrentUser(announceId, quizId, email);
//    }
//
//    @PostMapping("/attempts/{attemptId}/submit-raw")
//    public QuizAttempt submitRaw(@PathVariable String attemptId,
//                                 @RequestBody List<AttemptAnswer> answers,
//                                 Authentication authentication) {
//        String email = authentication.getName();
//        return quizFlowService.submitRawAnswersForCurrentUser(attemptId, answers, email);
//    }





//    @PostMapping("/announces/{announceId}/quizzes/{quizId}/start")
//    public AttemptDTO startAttempt(@PathVariable String announceId,
//                                   @PathVariable String quizId,
//                                   Authentication authentication) {
//        String email = authentication.getName();
//        QuizAttempt a = quizFlowService.startAttemptForCurrentUser(announceId, quizId, email);
//        return AttemptDTO.from(a);                       // <-- ICI
//    }
//
//    @PostMapping("/attempts/{attemptId}/submit-raw")
//    public AttemptDTO submitRaw(@PathVariable String attemptId,
//                                @RequestBody List<AttemptAnswer> answers,
//                                Authentication authentication) {
//        String email = authentication.getName();
//        QuizAttempt a = quizFlowService.submitRawAnswersForCurrentUser(attemptId, answers, email);
//        return AttemptDTO.from(a);                       // <-- ICI
//    }
//
//    @GetMapping("/announces/{announceId}/attempts")
//    public List<QuizAttemptView> listAttemptsOfMyAnnounce(@PathVariable String announceId,
//                                                          Authentication authentication) {
//        String email = authentication.getName();
//        return quizFlowService.listSubmittedAttemptsViewForAnnounceOwnedBy(announceId, email);
//    }
//
//
//    @GetMapping("/announces/{announceId}/attempts")
//    public List<QuizAttemptView> listAttempts(@PathVariable String announceId,
//                                              Authentication authentication) {
//        String email = authentication.getName();
//        return quizFlowService.getSubmittedAttemptsForAnnounceOwnedBy(announceId, email)
//                .stream()
//                .map(QuizAttemptView::from)
//                .toList();
//    }
    @PostMapping("/announces/{announceId}/quizzes/{quizId}/start")
    public AttemptDTO startAttempt(@PathVariable String announceId,
                                   @PathVariable String quizId,
                                   Authentication authentication) {
        String email = authentication.getName();
        QuizAttempt a = quizFlowService.startAttemptForCurrentUser(announceId, quizId, email);
        return AttemptDTO.from(a);
    }

    @PostMapping("/attempts/{attemptId}/submit-raw")
    public AttemptDTO submitRaw(@PathVariable String attemptId,
                                @RequestBody List<AttemptAnswer> answers,
                                Authentication authentication) {
        String email = authentication.getName();
        QuizAttempt a = quizFlowService.submitRawAnswersForCurrentUser(attemptId, answers, email);
        return AttemptDTO.from(a);
    }

    // ✅ UNE SEULE route GET qui renvoie déjà les *views*
//    @GetMapping("/announces/{announceId}/attempts")
//    public List<QuizAttemptView> listAttempts(@PathVariable String announceId,
//                                              Authentication authentication) {
//        String email = authentication.getName();
//        return quizFlowService.listSubmittedAttemptsViewForAnnounceOwnedBy(announceId, email);
//    }
//
//
//    @GetMapping("/announces/{announceId}/quizzes/{quizId}/attempts")
//    public List<QuizAttemptView> listAttemptsForQuiz(@PathVariable String announceId,
//                                                     @PathVariable String quizId,
//                                                     Authentication authentication) {
//        String email = authentication.getName();
//        return quizFlowService.listSubmittedAttemptsViewForAnnounceAndQuizOwnedBy(announceId, quizId, email);
//    }
    @GetMapping("/announces/{announceId}/attempts")
    public List<QuizAttemptView> listAttempts(@PathVariable String announceId,
                                              Authentication authentication) {
        // plus d’authz propriétaire ici
        return quizFlowService.listSubmittedAttemptsViewForAnnounce(announceId);
    }

    // (optionnel) Résultats d’un quiz précis de l’annonce
    @GetMapping("/announces/{announceId}/quizzes/{quizId}/attempts")
    public List<QuizAttemptView> listAttemptsForQuiz(@PathVariable String announceId,
                                                     @PathVariable String quizId,
                                                     Authentication authentication) {
        return quizFlowService.listSubmittedAttemptsViewForAnnounceAndQuiz(announceId, quizId);
    }
}


