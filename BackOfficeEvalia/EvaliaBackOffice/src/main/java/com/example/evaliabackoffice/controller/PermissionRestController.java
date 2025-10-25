package com.example.evaliabackoffice.controller;


import com.example.evaliabackoffice.entity.Permission;
import com.example.evaliabackoffice.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin("http://localhost:4200")
//@RequestMapping("Permission")
//@RestController
public class PermissionRestController {
//    @Autowired
    IPermissionService iPermissionService;

    @PostMapping("/addPermission")
    public Permission addPermission(@RequestBody Permission permission) {
        return iPermissionService.addPermission(permission);

    }

    @GetMapping("/getAllPermissions")
    public List<Permission> getAllPermission(){
        return iPermissionService.getAllPermissions();

    }

    @GetMapping("/getDetailsPermission/{id}")
    public Permission getDetailsPermission(@PathVariable("id") Long id){
        return iPermissionService.DetailsPermission(id);
    }

    @PutMapping("/updatePermission/{id}")
    public Permission updatePermission(@RequestBody Permission permission,@PathVariable("id") Long id){
        return iPermissionService.updatePermission(permission,id);
    }

    @DeleteMapping("/deletePermission/{id}")
    public String deletePermission(@PathVariable("id") Long id){
        iPermissionService.deletePermission(id);
        return "Permission deleted";
    }








}
