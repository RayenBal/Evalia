package com.example.evaliabackoffice.service;


import com.example.evaliabackoffice.entity.Permission;
import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.repository.PermissionRepository;
import com.example.evaliabackoffice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
@Service
public class RoleService implements IRoleService{
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Role addRole(Role role) {

        List<Permission> attachedPermissions = new ArrayList<>();


        for (Permission p : role.getPermissions()) {
            Permission existingPermission = permissionRepository.findById(p.getIdPermission())
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + p.getIdPermission()));
            attachedPermissions.add(existingPermission);
        }

        role.setPermissions(attachedPermissions);
        return roleRepository.save(role);




    }

    @Override
    public void deleteRole(Long idRole) {
        if(!roleRepository.existsById(idRole)){
            throw new NoSuchElementException("Aucun rôle trouvé avec l'id : " + idRole);
        }
        roleRepository.deleteById(idRole);
    }

    @Override

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role DetailsRole(Long idRole) {
        return roleRepository.findById(idRole)
                .orElseThrow(() -> new NoSuchElementException("Rôle non trouvé avec l'id : " + idRole));
    }

    @Override
    public Role updateRole(Role role, Long id) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver le rôle à mettre à jour avec l'id : " + id));

        existingRole.setNameRole(role.getNameRole());
        existingRole.setDescription(role.getDescription());
        existingRole.setPermissions(role.getPermissions());

        return roleRepository.save(existingRole);
    }

    @Override
    public Role assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.getPermissions().addAll(permissions);
        return roleRepository.save(role);
    }
    }


