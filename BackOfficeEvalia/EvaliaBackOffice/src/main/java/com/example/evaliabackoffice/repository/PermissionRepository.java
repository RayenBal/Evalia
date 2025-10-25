package com.example.evaliabackoffice.repository;

import com.example.evaliabackoffice.entity.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    Optional<Permission> findByNamePermission(String namePermission);

    boolean existsByNamePermission(String namePermission);
}
