package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Campagne;
import com.example.evaliabackoffice.entity.CampagneStatus;
import com.example.evaliabackoffice.repository.CampagneRepository;
import com.example.evaliabackoffice.service.ICampagneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CampagneService implements ICampagneService {
    @Autowired
    CampagneRepository campagneRepository;
    @Override
    public Campagne addCampagne(Campagne campagne) {
        return campagneRepository.save(campagne);
    }

    @Override
    public void deleteCampagne(Long idCampagne) {
        campagneRepository.deleteById(idCampagne);
    }

    @Override
    public List<Campagne> getAllCampagnes() {
        return campagneRepository.findAll();
    }

    @Override
    public Campagne DetailsCampagne(Long idCampagne) {
        return campagneRepository.findById(idCampagne).get();
    }

    @Override
    public Campagne updateCampagne(Campagne campagne, Long id) {
        Campagne existingCampagne = campagneRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver la Campagne avec l'ID : " + id));

        existingCampagne.setNameCampagne(campagne.getNameCampagne());
        existingCampagne.setDescription(campagne.getDescription());

        return campagneRepository.save(existingCampagne);
    }

    public void updateCampagneStatus(Campagne campagne) {
        LocalDate today = LocalDate.now();
        if (today.isAfter(campagne.getEndDate())) {
            campagne.setStatus(CampagneStatus.ANNULEE);
        } else if (today.isBefore(campagne.getStartDate())) {
            campagne.setStatus(CampagneStatus.EN_COURS); // ou "PLANIFIEE" si tu veux encore plus de précision
        }
        campagneRepository.save(campagne);
    }
    // ✅ Tâche planifiée chaque jour à minuit
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAllCampagnesStatus() {
        List<Campagne> campagnes = campagneRepository.findAll();
        for (Campagne campagne : campagnes) {
            updateCampagneStatus(campagne);
        }
    }
}