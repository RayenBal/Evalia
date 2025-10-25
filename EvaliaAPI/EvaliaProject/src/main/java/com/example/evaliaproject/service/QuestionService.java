package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.Question;

import com.example.evaliaproject.entity.ResponsePaneliste;
import com.example.evaliaproject.repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class QuestionService implements IQuestionService{

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question addQuestion(Question question) {
        // assurer la cohérence des relations
        if (question.getResponses() != null) {
            for (ResponsePaneliste rep : question.getResponses()) {
                rep.setQuestion(question);
            }
        }
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(String id, Question newQuestion) {
        Question existing = questionRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setContent(newQuestion.getContent());
        existing.setResponses(newQuestion.getResponses());
        return addQuestion(existing); // remet à jour les réponses
    }

    @Override
    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    @Override
    public Question getQuestionById(String id) {
        return questionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> getQuestionsByQuizId(String idQuiz) {
        return questionRepository.findByQuiz_idQuiz(idQuiz);
    }












//    QuestionRepository questionRepository;
//    @Override
//    public Question addQuestion(Question question) {
//        return questionRepository.save(question);
//    }
//
//    @Override
//    public void deleteQuestion(Long idQuestion) {
//    questionRepository.deleteById(idQuestion);
//    }
//
//    @Override
//    public List<Question> getAllQuestions() {
//        return questionRepository.findAll();
//    }
//
//    @Override
//    public Question DetailsQuestion(Long idQuestion) {
//        return questionRepository.findById(idQuestion).get();
//    }
//
//    @Override
//    public Question updateUser(Question question, Long id) {
//        Question existingQuestion = questionRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver la Question avec l'ID : " + id));
//
//        existingQuestion.setContent(question.getContent());
//
//
//        return questionRepository.save(existingQuestion);
//    }



}
