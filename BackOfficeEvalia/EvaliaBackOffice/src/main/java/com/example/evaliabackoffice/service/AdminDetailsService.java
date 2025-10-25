package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Admin;
import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            throw new UsernameNotFoundException("Stored password missing for: " + email);
        }
        return User.withUsername(admin.getEmail())
                .password(admin.getPassword())
                .roles(admin.getRoles().stream()
                        .map(Role::getNameRole) // e.g., "SUPER_ADMIN"
                        .toArray(String[]::new))
                .build();
    }
}