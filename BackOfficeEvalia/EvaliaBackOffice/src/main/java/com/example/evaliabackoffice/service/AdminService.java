package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Admin;
import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;



@Service
public class AdminService implements IAdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AdminRepository adminRepository;
    @Override
    public Admin addAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setConfirmPassword(passwordEncoder.encode(admin.getConfirmPassword()));

        return adminRepository.save(admin);
    }



    @Override
    public void deleteAdmin(Long idadmin) {
         adminRepository.deleteById(idadmin);

    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }



    @Override
    public Admin DetailsAdmin(Long idAdmin) {
        return adminRepository.findById(idAdmin).get();
    }

    @Override
    public Admin updateAdmin(Admin admin, Long id) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver l'admin à mettre à jour avec l'id : " + id));

        existingAdmin.setFirstname(admin.getFirstname());
        existingAdmin.setLastname(admin.getLastname());
        existingAdmin.setEmail(admin.getEmail());
        existingAdmin.setPassword(admin.getPassword());
        existingAdmin.setConfirmPassword(admin.getConfirmPassword());
        existingAdmin.setRoles(admin.getRoles());
        return adminRepository.save(existingAdmin);
    }




}
