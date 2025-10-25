package com.example.evaliaproject.service;


import com.example.evaliaproject.entity.Recompenses;

import java.util.List;

public interface IRecompensesService {
    public Recompenses addRecompenses(Recompenses recompenses);


    void deleteRecompenses(String idRrecompenses);

    public List<Recompenses> getAllRecompenses();


    Recompenses DetailsRecompenses(String idRecompenses);

    Recompenses updateRecompenses(Recompenses recompenses, String id);

    List<Recompenses> byAnnouncement(String id);
}
