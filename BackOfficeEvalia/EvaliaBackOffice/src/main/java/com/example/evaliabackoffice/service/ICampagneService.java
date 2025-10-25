package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Campagne;

import java.util.List;

public interface ICampagneService {
    public Campagne addCampagne(Campagne campagne);


    void deleteCampagne(Long idCampagne);

    public List<Campagne> getAllCampagnes();


    Campagne DetailsCampagne(Long idCampagne);

    Campagne updateCampagne(Campagne campagne, Long id);
}
