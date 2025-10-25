package com.example.evaliaproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.evaliaproject.entity.Recompenses;
import com.example.evaliaproject.repository.RecompensesRepository;

import java.util.List;
import java.util.NoSuchElementException;
@Service
public class RecompensesService implements IRecompensesService{
    @Autowired
    RecompensesRepository recompensesRepository;

    @Override
    public Recompenses addRecompenses(Recompenses recompenses) {
        return recompensesRepository.save(recompenses);
    }

    @Override
    public void deleteRecompenses(String idRecompenses) {
      recompensesRepository.deleteById(idRecompenses);
    }

    @Override
    public List<Recompenses> getAllRecompenses() {
        return recompensesRepository.findAll();
    }

    @Override
    public Recompenses DetailsRecompenses(String idRecompenses) {
        return recompensesRepository.findById(idRecompenses).get();
    }

    @Override
    public Recompenses updateRecompenses(Recompenses body, String id) {
        Recompenses existing = recompensesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Récompense introuvable: " + id));

        // ✅ Met à jour TOUTES les colonnes modifiables
        existing.setTypeRecompenses(body.getTypeRecompenses());
        existing.setAmount(body.getAmount());
        existing.setLabel(body.getLabel());

        return recompensesRepository.save(existing);
    }

    public List<Recompenses> byAnnouncement(String announcementId) {
        return recompensesRepository.findByAnnouncement_IdAnnouncement(announcementId);
    }
}
