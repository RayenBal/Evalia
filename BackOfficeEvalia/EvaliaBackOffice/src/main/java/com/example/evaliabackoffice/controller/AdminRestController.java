package com.example.evaliabackoffice.controller;

import com.example.evaliabackoffice.entity.Admin;
import com.example.evaliabackoffice.service.IAdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("Admin11")
@RestController
public class AdminRestController {
    @Autowired
    IAdminService iAdminService;





    @PostMapping("/addAdmin")
    public Admin addAdmin(@RequestBody Admin admin) {
        if (admin.getRoles() == null) {
            admin.setRoles(new ArrayList<>());
        }
        return iAdminService.addAdmin(admin);

    }

    @GetMapping("/getAllAdmins")
    public List<Admin> getAllAdmins(){
        return iAdminService.getAllAdmins();

    }

    @GetMapping("/getDetailsAdmin/{id}")
    public Admin getDetailsAdmin(@PathVariable("id") Long id){
        return iAdminService.DetailsAdmin(id);
    }

    @PutMapping("/updateAdmin/{id}")
    public Admin updateAdmin (@RequestBody Admin admin,@PathVariable("id") Long id){
        return iAdminService.updateAdmin(admin,id);
    }

    @DeleteMapping("/deleteAdmin/{id}")
    public String deleteAdmin(@PathVariable("id") Long id){
        iAdminService.deleteAdmin(id);
        return "Admin deleted";
    }
}
