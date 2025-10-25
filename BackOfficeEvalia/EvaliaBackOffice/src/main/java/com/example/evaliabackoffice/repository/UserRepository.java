package com.example.evaliabackoffice.repository;

import com.example.evaliabackoffice.entity.User;
import com.example.evaliabackoffice.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
    public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByEnabledFalseAndNeedsAdminValidationTrue();


    // ► listes par statut
    List<User> findAllByStatusOrderByCreatedDateDesc(UserStatus status);

    // optionnel si tu veux filtrer en plus par enabled
    List<User> findAllByStatusAndEnabledTrueOrderByCreatedDateDesc(UserStatus status);



}
