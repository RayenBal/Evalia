package com.example.evaliaproject.service;



import com.example.evaliaproject.entity.Question;

import java.util.List;

public interface IQuestionService {
//    public Question addQuestion(Question question);
//
//
//    void deleteQuestion(Long idQuestion);
//
//    public List<Question> getAllQuestions();
//
//
//    Question DetailsQuestion(Long idQuestion);
//
//    Question updateUser(Question question, Long id);
Question addQuestion(Question question);
    Question updateQuestion(String id, Question question);
    void deleteQuestion(String id);
    Question getQuestionById(String id);
    List<Question> getAllQuestions();
    List<Question> getQuestionsByQuizId(String quizId);
}
