package com.example.evaliaproject.service;



import com.example.evaliaproject.entity.Quiz;
import com.example.evaliaproject.entity.ResponsePaneliste;

import java.util.List;

public interface IQuizService {
    public Quiz addQuiz(Quiz quiz, String announceId);


    void deleteQuiz(String idQuiz);

    public List<Quiz> getAllQuiz();


    Quiz DetailsQuiz(String idQuiz);

    Quiz updateQuiz(Quiz quiz, String id);

    void submitResponses(String id, List<ResponsePaneliste> responses);
}
