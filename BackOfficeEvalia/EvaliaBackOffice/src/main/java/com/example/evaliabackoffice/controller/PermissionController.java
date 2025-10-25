package com.example.evaliabackoffice.controller;

import com.example.evaliabackoffice.entity.Permission;
import com.example.evaliabackoffice.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("permission", new Permission());
        return "create_permission";
    }

    @GetMapping("/createPR")
    public String showCreateFormPR(Model model) {
        model.addAttribute("permission", new Permission());
        return "create_permission_role";
    }
    @GetMapping("/test")
    public String test() {
        return "permissions";
    }
    @PostMapping("/save")


    public String savePermission(@ModelAttribute("permission") Permission permission) {
        permissionRepository.save(permission);
        return "redirect:/permissions";
    }


    @PostMapping("/saveR")
    public String savePermissionRole(@ModelAttribute("permission") Permission permission) {
        permissionRepository.save(permission);
        return "redirect:/roles";
    }
    @GetMapping
    public String listPermissions(Model model) {
        model.addAttribute("permissions", permissionRepository.findAll());
        return "permissions";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission non trouvée : " + id));
        model.addAttribute("permission", permission);
        return "edit_permission";
    }


    @PostMapping("/update")
    public String updatePermission(@ModelAttribute Permission permission) {
        permissionRepository.save(permission);
        return "redirect:/permissions";
    }


    @GetMapping("/delete/{id}")
    public String deletePermission(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return "redirect:/permissions";
    }



}