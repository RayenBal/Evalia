package com.example.evaliaproject.service;



import com.example.evaliaproject.entity.Reclamation;

import java.util.List;

public interface IReclamationService {

    Reclamation addReclamation(Reclamation reclamation);
    void deleteReclamation(String idReclamation);
    List<Reclamation> getAllReclamations();
    Reclamation DetailsReclamation(String idReclamation);
    Reclamation updateReclamation(Reclamation reclamation, String id);

    // pratique pour l’UI
    List<Reclamation> listMine();
}
