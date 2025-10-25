package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.Recompenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecompensesRepository extends JpaRepository<Recompenses,String> {
    List<Recompenses> findByAnnouncement_IdAnnouncement(String announcementId);

}
