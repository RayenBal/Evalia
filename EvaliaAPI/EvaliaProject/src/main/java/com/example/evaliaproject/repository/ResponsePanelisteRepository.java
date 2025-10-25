package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.ResponsePaneliste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ResponsePanelisteRepository extends JpaRepository<ResponsePaneliste,String> {
    List<ResponsePaneliste> findByQuestion_idQuestion(String idQuestion);
}
