package com.example.evaliabackoffice.config;

import com.example.evaliabackoffice.entity.Admin;
import com.example.evaliabackoffice.repository.AdminRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


import java.io.IOException;
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler  {
    @Autowired
private AdminRepository adminRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        Admin admin = adminRepository.findByEmail(email).orElse(null);

        if (admin != null) {
            boolean isSuperAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_SUPER_ADMIN"));

            if (isSuperAdmin) {
                response.sendRedirect("/admin/list"); // 👉 SUPER_ADMIN → list
            } else {
                response.sendRedirect("/admin/details/" + admin.getIdadmin()); // 👉 ADMIN → profile
            }
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}