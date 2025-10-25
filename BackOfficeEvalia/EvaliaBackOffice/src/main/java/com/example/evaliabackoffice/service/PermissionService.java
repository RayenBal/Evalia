package com.example.evaliabackoffice.service;


import com.example.evaliabackoffice.entity.Permission;
import com.example.evaliabackoffice.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
@Service
public class PermissionService implements IPermissionService{

    @Autowired
    private PermissionRepository permissionRepository;
    @Override
    public Permission addPermission(Permission permission) {
        if (permissionRepository.existsByNamePermission(permission.getNamePermission())) {
            throw new IllegalArgumentException("La permission existe déjà : " + permission.getNamePermission());
        }
        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Long idPermission) {
        if (!permissionRepository.existsById(idPermission)) {
            throw new NoSuchElementException("Permission introuvable avec l'ID : " + idPermission);
        }
        permissionRepository.deleteById(idPermission);

    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission DetailsPermission(Long idPermission) {
        return permissionRepository.findById(idPermission)
                .orElseThrow(() -> new NoSuchElementException("Permission introuvable avec l'ID : " + idPermission));
    }

    @Override
    public Permission updatePermission(Permission permission, Long idPermission) {
        Permission existingPermission = permissionRepository.findById(idPermission)
                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver la permission avec l'ID : " + idPermission));

        existingPermission.setNamePermission(permission.getNamePermission());
        existingPermission.setDescription(permission.getDescription());

        return permissionRepository.save(existingPermission);
    }

}
