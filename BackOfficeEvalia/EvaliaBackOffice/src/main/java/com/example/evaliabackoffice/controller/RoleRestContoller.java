package com.example.evaliabackoffice.controller;


import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin("http://localhost:4200")
//@RequestMapping("Role")
//@RestController
public class RoleRestContoller {
//    @Autowired
    IRoleService iRoleService;

    @PostMapping("/addRole")
    public Role addRole(@RequestBody Role role) {
            return iRoleService.addRole(role);

    }

    @GetMapping("/getAllRoles")
    public List<Role> getAllRoles(){

        return iRoleService.getAllRoles();

    }

    @GetMapping("/getDetailsRole/{id}")
    public Role getDetailsRole(@PathVariable("id") Long id){
     return iRoleService.DetailsRole(id);
    }

    @PutMapping("/updateRole/{id}")
    public Role updateRole (@RequestBody Role role,@PathVariable("id") Long id){
        return iRoleService.updateRole(role,id);
    }

    @DeleteMapping("/deleteRole/{id}")
    public String deleteRole(@PathVariable("id") Long id){
        iRoleService.deleteRole(id);
        return "Role deleted";
    }

    @PostMapping("/assignPermissionsToRole/{roleId}")
    public Role assignPermissionsToRole(
            @PathVariable("roleId") Long roleId,
            @RequestBody List<Long> permissionIds
    ) {
        Role updatedRole = iRoleService.assignPermissionsToRole(roleId, permissionIds);
        return updatedRole;    }

}
