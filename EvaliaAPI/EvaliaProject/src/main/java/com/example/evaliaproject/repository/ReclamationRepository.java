package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation,String> {
    @Query("select r from Reclamation r where r.user.id_user = :uid")
    List<Reclamation> findAllByUserId(@Param("uid") Long uid);

}
