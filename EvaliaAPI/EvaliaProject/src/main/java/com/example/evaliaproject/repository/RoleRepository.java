package com.example.evaliaproject.repository;


import com.example.evaliaproject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
   Role findByNameRole(String nameRole);
    boolean existsByNameRole(String nameRole);
}
