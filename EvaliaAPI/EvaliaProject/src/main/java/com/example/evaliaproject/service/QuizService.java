package com.example.evaliaproject.service;


import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Question;
import com.example.evaliaproject.entity.Quiz;
import com.example.evaliaproject.entity.ResponsePaneliste;
import com.example.evaliaproject.repository.AnnouncementRepository;
import com.example.evaliaproject.repository.QuizRepository;
import com.example.evaliaproject.repository.ResponsePanelisteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
@Service
public class QuizService implements IQuizService{
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    ResponsePanelisteRepository responsePanelisteRepository;
    @Autowired
    QuizRepository quizRepository;
    @Override
    public Quiz addQuiz(Quiz quiz,String announceId) {
        // Lier l'annonce si announceId fourni
        if (announceId != null && !announceId.isBlank()) {
            Announce announce = announcementRepository.findById(announceId)
                    .orElseThrow(() -> new NoSuchElementException("Annonce introuvable: " + announceId));
            quiz.setAnnouncement(announce);
        } else if (quiz.getAnnouncement() != null && quiz.getAnnouncement().getIdAnnouncement() != null) {
            // cas où le front envoie déjà {announcement:{idAnnouncement:"..."}}
            String id = quiz.getAnnouncement().getIdAnnouncement();
            Announce announce = announcementRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Annonce introuvable: " + id));
            quiz.setAnnouncement(announce); // ré-attache l’entité managée
        } else {
            throw new IllegalArgumentException("announceId manquant pour lier le quiz à une annonce");
        }

        // Back-refs
        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(quiz);
                if (question.getResponses() != null) {
                    for (ResponsePaneliste r : question.getResponses()) {
                        r.setQuestion(question);
                    }
                }
            }
        }
        return quizRepository.save(quiz);
    }


//    public Quiz addQuiz(Quiz quiz) {
//
//
////        for (Question question : quiz.getQuestions()) {
////            question.setQuiz(quiz); // lien bidirectionnel avec le quiz
////
////            for (ResponsePaneliste response : question.getResponses()) {
////                response.setQuestion(question); // ⚠️ lien obligatoire
////            }
////        }
//        for (Question question : quiz.getQuestions()) {
//            question.setQuiz(quiz); // relation bidirectionnelle
//            if (question.getResponses() == null) {
//                question.setResponses(new ArrayList<>());
//            }
//            for (ResponsePaneliste reponse : question.getResponses()) {
//                reponse.setQuestion(question); // relation bidirectionnelle
//            }
//        }
//
//        return quizRepository.save(quiz);
//    }

    @Override
    public void deleteQuiz(String idQuiz) {
      quizRepository.deleteById(idQuiz);
    }

    @Override
    public List<Quiz> getAllQuiz() {
        return quizRepository.findAll();
    }

//    @Override
//    public Quiz DetailsQuiz(String idQuiz) {
//        return quizRepository.findById(idQuiz).get();
//    }

//    @Override
//    public Quiz updateQuiz(Quiz quiz, String id) {
//        Quiz existingQuiz = quizRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver le Quiz avec l'ID : " + id));
//
//        existingQuiz.setContent(quiz.getContent());
//
////        for (Question question : quiz.getQuestions()) {
////            question.setQuiz(quiz); // lien bidirectionnel avec le quiz
////
////            for (ResponsePaneliste response : question.getResponses()) {
////                response.setQuestion(question); // ⚠️ lien obligatoire
////            }
////        }
//        return quizRepository.save(existingQuiz);
//    }
    @Override
    public void submitResponses(String quizId, List<ResponsePaneliste> responses) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz introuvable avec ID : " + quizId));

        for (ResponsePaneliste response : responses) {
            // ⚠️ Vérifie qu'une question est bien liée à chaque réponse
            if (response.getQuestion() == null || response.getQuestion().getIdQuestion() == null) {
                throw new IllegalArgumentException("Chaque réponse doit être liée à une question.");
            }

            // 🔄 Ici tu pourrais ajouter une logique pour éviter les doublons ou vérifier les permissions

            // Enregistre la réponse
            responsePanelisteRepository.save(response);
        }
    }



    @Override
    @Transactional

    public Quiz updateQuiz(Quiz quiz, String id) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver le Quiz avec l'ID : " + id));

        // 1) Champs simples
        existingQuiz.setContent(quiz.getContent());

        // 2) Remplacer proprement la liste des questions (orphanRemoval = true => supprime celles absentes)
        existingQuiz.getQuestions().clear();

        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                // back-ref obligatoire
                question.setQuiz(existingQuiz);

                // Responses
                if (question.getResponses() != null) {
                    for (ResponsePaneliste response : question.getResponses()) {
                        // back-ref obligatoire
                        response.setQuestion(question);
                    }
                } else {
                    question.setResponses(new ArrayList<>());
                }

                existingQuiz.getQuestions().add(question);
            }
        }
        return quizRepository.save(existingQuiz);
    }
    @Override
    @Transactional(readOnly = true)
    public Quiz DetailsQuiz(String idQuiz) {
        Quiz q = quizRepository.fetchWithQuestions(idQuiz)
                .orElseThrow(() -> new NoSuchElementException("Quiz introuvable: " + idQuiz));

        // 2ème passe : charge les réponses SANS fetch-join (grâce à @Fetch(SUBSELECT))
        q.getQuestions().forEach(qu -> qu.getResponses().size());

        return q;
    }
}
