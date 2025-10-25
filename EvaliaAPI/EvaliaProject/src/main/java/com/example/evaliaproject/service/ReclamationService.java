package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.Quiz;
import com.example.evaliaproject.entity.Reclamation;
import com.example.evaliaproject.repository.ReclamationRepository;
import com.example.evaliaproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReclamationService implements IReclamationService {
    @Autowired
    ReclamationRepository reclamationRepository;
     @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public Reclamation addReclamation(Reclamation reclamation) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + email));
        reclamation.setUser(user);
        reclamation.setUserType(user.getTypeUser());


        return reclamationRepository.save(reclamation);
    }

    @Override
    public void deleteReclamation(String idReclamation) {
        reclamationRepository.deleteById(idReclamation);
    }

    @Override
    public List<Reclamation> getAllReclamations() { return reclamationRepository.findAll(); }

    @Override
    public Reclamation DetailsReclamation(String id) {
        return reclamationRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Reclamation updateReclamation(Reclamation r, String id) {
        var ex = reclamationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reclamation " + id + " introuvable"));

        ex.setContent(r.getContent());
        ex.setMotif(r.getMotif());
        ex.setUserType(ex.getUser().getTypeUser());    // <— garde le champ synchro

        return reclamationRepository.save(ex);
    }

    // pratique : réclamations du user connecté
    public List<Reclamation> listMine() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow();
        return reclamationRepository.findAllByUserId(user.getId_user());
    }
}