package com.example.evaliabackoffice.repository;

import com.example.evaliabackoffice.entity.Campagne;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampagneRepository extends JpaRepository<Campagne,Long> {
}
