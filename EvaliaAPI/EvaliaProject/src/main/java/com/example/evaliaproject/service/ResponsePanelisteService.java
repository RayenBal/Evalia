package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.ResponsePaneliste;
import com.example.evaliaproject.repository.ResponsePanelisteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ResponsePanelisteService  implements IResponsePanelisteService{
    @Autowired
    private ResponsePanelisteRepository responsePanelisteRepository;

    @Override
    public ResponsePaneliste addReponse(ResponsePaneliste reponse) {
        return responsePanelisteRepository.save(reponse);
    }

    @Override
    public ResponsePaneliste updateReponse(String id, ResponsePaneliste updated) {
        ResponsePaneliste r = responsePanelisteRepository.findById(id).orElse(null);
        if (r == null) return null;
        r.setContent(updated.getContent());

        return responsePanelisteRepository.save(r);
    }

    @Override
    public void deleteReponse(String id) {
        responsePanelisteRepository.deleteById(id);
    }

    @Override
    public ResponsePaneliste getReponseById(String id) {
        return responsePanelisteRepository.findById(id).orElse(null);
    }

    @Override
    public List<ResponsePaneliste> getAllReponses() {
        return responsePanelisteRepository.findAll();
    }

    @Override
    public List<ResponsePaneliste> getByQuestionId(String idQuestion) {
        return responsePanelisteRepository.findByQuestion_idQuestion (idQuestion);
    }
}
