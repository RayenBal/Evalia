package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.ResponsePaneliste;

import java.util.List;

public interface IResponsePanelisteService {
    ResponsePaneliste addReponse(ResponsePaneliste reponse);
    ResponsePaneliste updateReponse(String id, ResponsePaneliste reponse);
    void deleteReponse(String id);
    ResponsePaneliste getReponseById(String id);
    List<ResponsePaneliste> getAllReponses();
//    List<ResponsePaneliste> getByQuestionId(String questionId);
public List<ResponsePaneliste> getByQuestionId(String questionId);
}
