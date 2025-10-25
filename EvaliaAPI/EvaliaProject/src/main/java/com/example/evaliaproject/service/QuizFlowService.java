package com.example.evaliaproject.service;



import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AccessDeniedException;
import com.example.evaliaproject.entity.*;
import com.example.evaliaproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class QuizFlowService {

        private final AnnouncementRepository announcementRepository;
        private final QuizRepository quizRepository;
        private final QuizAttemptRepository attemptRepository;
        private final AttemptAnswerRepository answerRepository;
        private final QuestionRepository questionRepository;
        private final ResponsePanelisteRepository responseRepository;
        private final UserRepository userRepository;


        // ⚠️ Note : l'attribution de la récompense se fait maintenant
        //           dans FeedbackService.createForAttempt(attemptId, feedback),
        //           au moment où le paneliste envoie son feedback.
        @Transactional
        public QuizAttempt startAttemptForCurrentUser(String announceId, String quizId, String email) {
            User panelist = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));

            if (panelist.getTypeUser() != TypeUser.Paneliste) {
                throw new AccessDeniedException("Seuls les panelistes peuvent répondre aux quiz.");
            }

            Announce announce = announcementRepository.findById(announceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable: " + announceId));

            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new IllegalArgumentException("Quiz introuvable: " + quizId));

            if (quiz.getAnnouncement() == null
                    || !quiz.getAnnouncement().getIdAnnouncement().equals(announce.getIdAnnouncement())) {
                throw new IllegalStateException("Ce quiz n'appartient pas à l'annonce fournie.");
            }

            QuizAttempt attempt = QuizAttempt.builder()
                    .announcement(announce)
                    .quiz(quiz)
                    .panelist(panelist)
                    .status(AttemptStatus.STARTED)
                    .rewardGranted(false)
                    .build();
            return attemptRepository.save(attempt);
        }

        @Transactional
        public QuizAttempt submitRawAnswersForCurrentUser(String attemptId,
                                                          List<AttemptAnswer> incomingAnswers,
                                                          String email) {
            User current = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
            QuizAttempt attempt = attemptRepository.findById(attemptId)
                    .orElseThrow(() -> new IllegalArgumentException("Tentative introuvable: " + attemptId));

            // sécurité: l’auteur de la tentative doit être le user connecté
            if (!attempt.getPanelist().getId_user().equals(current.getId_user())) {
                throw new AccessDeniedException("Vous ne pouvez soumettre que vos propres tentatives.");
            }
            if (current.getTypeUser() != TypeUser.Paneliste) {
                throw new AccessDeniedException("Seuls les panelistes peuvent soumettre.");
            }

            // ... (même logique que ta méthode submitRawAnswers existante)
            attempt.getAnswers().clear();
            for (AttemptAnswer raw : incomingAnswers) {
                String qId = raw.getQuestion() != null ? raw.getQuestion().getIdQuestion() : null;
                String rId = raw.getSelectedResponse() != null ? raw.getSelectedResponse().getIdResponsePaneliste() : null;
                if (qId == null || rId == null) throw new IllegalArgumentException("IDs requis.");

                Question question = questionRepository.findById(qId)
                        .orElseThrow(() -> new IllegalArgumentException("Question introuvable: " + qId));
                ResponsePaneliste selected = responseRepository.findById(rId)
                        .orElseThrow(() -> new IllegalArgumentException("Réponse introuvable: " + rId));
                if (!selected.getQuestion().getIdQuestion().equals(question.getIdQuestion()))
                    throw new IllegalArgumentException("Réponse ≠ question.");

                AttemptAnswer answer = AttemptAnswer.builder()
                        .attempt(attempt).question(question).selectedResponse(selected)
                        .freeText(raw.getFreeText())
                        .build();
                attempt.getAnswers().add(answer);
            }
            attempt.setStatus(AttemptStatus.SUBMITTED);
            attempt.setSubmittedAt(java.time.LocalDateTime.now());
            return attemptRepository.save(attempt);
        }


            @Transactional(readOnly = true)
            public List<QuizAttempt> findSubmittedAttemptsForAnnounceOwnedBy(String announceId, String email) {
                User current = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));

                if (current.getTypeUser() != TypeUser.Announceur) {
                    throw new org.springframework.security.access.AccessDeniedException("Réservé aux annonceurs.");
                }

                Announce ann = announcementRepository.findById(announceId)
                        .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable: " + announceId));

                boolean ownedByUser  = ann.getUser()  != null && ann.getUser().getId_user().equals(current.getId_user());
                boolean ownedByAdmin = ann.getAdmin() != null
                        && ann.getAdmin().getEmail() != null
                        && ann.getAdmin().getEmail().equalsIgnoreCase(current.getEmail());

                if (!ownedByUser && !ownedByAdmin) {
                    throw new org.springframework.security.access.AccessDeniedException("Vous devez être l’annonceur propriétaire.");
                }

                // Tout est fetch-join dans le repo
                return attemptRepository.findSubmittedWithAnswersByAnnouncement(announceId);
            }

            @Transactional(readOnly = true)
            public List<QuizAttemptView> listSubmittedAttemptsViewForAnnounceOwnedBy(String announceId, String email) {
                return findSubmittedAttemptsForAnnounceOwnedBy(announceId, email)
                        .stream()
                        .map(QuizAttemptView::from)
                        .toList();
            }
        @Transactional(readOnly = true)
        public List<QuizAttemptView> listSubmittedAttemptsViewForAnnounceAndQuizOwnedBy(
                String announceId, String quizId, String email) {

            // 1) AuthN + AuthZ : l'appelant doit être l'annonceur propriétaire (ou admin attaché)
            User current = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
            if (current.getTypeUser() != TypeUser.Announceur) {
                throw new AccessDeniedException("Réservé aux annonceurs.");
            }

            Announce ann = announcementRepository.findById(announceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable: " + announceId));

            boolean ownedByUser  = ann.getUser()  != null && ann.getUser().getId_user().equals(current.getId_user());
            boolean ownedByAdmin = ann.getAdmin() != null
                    && ann.getAdmin().getEmail() != null
                    && ann.getAdmin().getEmail().equalsIgnoreCase(current.getEmail());

//            if (!ownedByUser && !ownedByAdmin) {
//                throw new AccessDeniedException("Vous devez être l’annonceur propriétaire.");
//            }

            // 2) Sanity check : le quiz doit appartenir à cette annonce
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new IllegalArgumentException("Quiz introuvable: " + quizId));
            if (quiz.getAnnouncement() == null ||
                    !announceId.equals(quiz.getAnnouncement().getIdAnnouncement())) {
                throw new IllegalStateException("Ce quiz n'appartient pas à l'annonce fournie.");
            }

            // 3) Récupération des tentatives filtrées par annonce + quiz
            return attemptRepository
                    .findSubmittedWithAnswersByAnnouncementAndQuiz(announceId, quizId)
                    .stream()
                    .map(QuizAttemptView::from)
                    .toList();
        }








        // ⚠️ OUVERT : sans contrôle de propriétaire
        @Transactional(readOnly = true)
        public List<QuizAttemptView> listSubmittedAttemptsViewForAnnounce(String announceId) {
            return attemptRepository
                    .findSubmittedWithAnswersByAnnouncement(announceId)
                    .stream()
                    .map(QuizAttemptView::from)
                    .toList();
        }

        // ⚠️ OUVERT : sans contrôle de propriétaire
        @Transactional(readOnly = true)
        public List<QuizAttemptView> listSubmittedAttemptsViewForAnnounceAndQuiz(
                String announceId, String quizId) {

            // Sanity check utile pour éviter les confusions entre quiz et annonce
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new IllegalArgumentException("Quiz introuvable: " + quizId));
            if (quiz.getAnnouncement() == null ||
                    !announceId.equals(quiz.getAnnouncement().getIdAnnouncement())) {
                throw new IllegalStateException("Ce quiz n'appartient pas à l'annonce fournie.");
            }

            return attemptRepository
                    .findSubmittedWithAnswersByAnnouncementAndQuiz(announceId, quizId)
                    .stream()
                    .map(QuizAttemptView::from)
                    .toList();
        }

        }

