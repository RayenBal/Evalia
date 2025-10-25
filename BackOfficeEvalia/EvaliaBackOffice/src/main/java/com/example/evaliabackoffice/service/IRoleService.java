package com.example.evaliabackoffice.service;


import com.example.evaliabackoffice.entity.Role;

import java.util.List;

public interface IRoleService {
    public Role addRole(Role role);
    public void deleteRole(Long idRole);
    public List<Role> getAllRoles();
    Role DetailsRole(Long idRole);
    Role updateRole(Role role,Long id);
    Role assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}
