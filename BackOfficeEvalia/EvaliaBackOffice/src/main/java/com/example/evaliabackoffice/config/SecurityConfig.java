package com.example.evaliabackoffice.config;

import com.example.evaliabackoffice.service.AdminDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    AdminDetailsService adminDetailsService;
//    @Bean("backOfficeAuthManager")
//    @Primary
//    public AuthenticationManager backOfficeAuthManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .authenticationProvider(authenticationProviderAdmin())
//                .build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean("adminAuthProvider")
    public DaoAuthenticationProvider authenticationProviderAdmin() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(adminDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfig(CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }
    @Bean

    public SecurityFilterChain securityFilterChainAdmin(HttpSecurity http) throws Exception {
        return http

                .csrf(csrf -> csrf.disable())
                .userDetailsService(adminDetailsService)
                .authorizeHttpRequests(auth -> auth

                        // ✅ Public Pages
                        .requestMatchers("/", "/login", 
                                "/css/**", "/js/**", "/images/**", "/fonts/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()

                        // ✅ Admin management - SUPER_ADMIN only
                        .requestMatchers("/admin/register", "/admin/delete/**", "/admin/list", "/admin/register", "/admin/update")
                        .hasRole("SUPER_ADMIN")
                                .requestMatchers("/adminn/pending-users",
                                        "/adminn/validate-user/**",
                                        "/adminn/reject-user/**")
                                .hasRole("ADMINVALIDATION")

                                // ✅ Admin edit/detail - any authenticated admin
                        .requestMatchers("/admin/edit/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/admin/profile/edit/**", "/admin/profile/update", "/admin/details/**").authenticated()
                        .requestMatchers("/categories/addCategory","categories/getAllCategory").permitAll()
                        .requestMatchers("/adminn/registre/**").permitAll()
                        .requestMatchers("/bo/reclamations/**")
                       .hasAnyRole("ADMIN_RECLAMATION","SUPER_ADMIN")
                        // ✅ Categories management - authenticated admins
                        .requestMatchers("/categories", "/categories/**").authenticated()
                        
                        // ✅ Campagnes management - authenticated admins
                        .requestMatchers("/campagnes", "/campagnes/**").authenticated()

                        // ✅ Role management - SUPER_ADMIN only
                        .requestMatchers(
                                "/roles", "/roles/create", "/roles/save",
                                "/roles/edit/**", "/roles/update", "/roles/delete/**")
                        .hasRole("SUPER_ADMIN")

                        // ✅ Permission management - SUPER_ADMIN only
                        .requestMatchers(
                                "/permissions", "/permissions/create", "/permissions/createPR", "/permissions/test",
                                "/permissions/save", "/permissions/saveR",
                                "/permissions/edit/**", "/permissions/update", "/permissions/delete/**")
                        .hasRole("SUPER_ADMIN")

                        // ✅ Everything else
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customLoginSuccessHandler)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                .build();

    }


}
