package com.example.evaliabackoffice.repository;

import com.example.evaliabackoffice.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByNameRole(String nameRole);
    boolean existsByNameRole(String nameRole);
}
