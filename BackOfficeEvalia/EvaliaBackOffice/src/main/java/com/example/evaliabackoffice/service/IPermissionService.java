package com.example.evaliabackoffice.service;



import com.example.evaliabackoffice.entity.Permission;

import java.util.List;

public interface IPermissionService {

    Permission addPermission(Permission permission);
    void deletePermission(Long idPermission);
    List<Permission> getAllPermissions();

    Permission DetailsPermission(Long idPermission);
    Permission updatePermission(Permission permission, Long idPermission);
}

