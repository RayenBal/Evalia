package com.example.evaliabackoffice.config;

import com.example.evaliabackoffice.entity.Admin;
import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.repository.AdminRepository;
import com.example.evaliabackoffice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Role superAdminRole = roleRepository.findByNameRole("SUPER_ADMIN");

            if (superAdminRole == null) {
                superAdminRole = new Role();
                superAdminRole.setNameRole("SUPER_ADMIN");
                superAdminRole.setDescription("Super administrator with full access");
                superAdminRole = roleRepository.save(superAdminRole);
            }

            Admin admin = new Admin();
            admin.setFirstname("Super");
            admin.setLastname("Admin");
            admin.setEmail("super@admin.com");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
            admin.setConfirmPassword(admin.getPassword());
            admin.setRoles(List.of(superAdminRole));

            adminRepository.save(admin);
            System.out.println("✅ Super admin created.");
        }
    }
}
