package com.example.evaliabackoffice.controller;

import com.example.evaliabackoffice.entity.Permission;
import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.repository.PermissionRepository;
import com.example.evaliabackoffice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;


    @GetMapping
    public String listRole(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "roles";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("permissions", permissionRepository.findAll());
        return "create_role";
    }

    @PostMapping("/save")
    public String saveRole(@ModelAttribute Role role, @RequestParam(value = "selectedPermissions", required = false) List<Long> selectedPermissions) {
        if (selectedPermissions != null) {
            List<Permission> perms = permissionRepository.findAllById(selectedPermissions);
            role.setPermissions(perms);
        } else {
            // pas de permission sélectionnée
            role.setPermissions(Collections.emptyList());
        }
        roleRepository.save(role);
        return "redirect:/roles";
    }




    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role non trouvée : " + id));
        List<Permission> allPermissions = permissionRepository.findAll();

        model.addAttribute("role", role);
        model.addAttribute("allPermissions", allPermissions);
        return "edit_role";
    }




    @PostMapping("/update")
    public String updateRole(@ModelAttribute Role role, @RequestParam(value = "selectedPermissions", required = false) List<Long> selectedPermissions) {
//        List<Permission> selectedPermissions = permissionRepository.findAllById(permissionIds);
//        role.setPermissions(selectedPermissions);

        if (selectedPermissions != null) {
            List<Permission> perms = permissionRepository.findAllById(selectedPermissions);
            role.setPermissions(perms);
        } else {
            role.setPermissions(Collections.emptyList());
        }
        roleRepository.save(role);
        return "redirect:/roles";
    }


    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return "redirect:/roles";
    }



}
