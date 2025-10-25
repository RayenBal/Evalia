package com.example.evaliaproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthentificationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChainUser(HttpSecurity http) throws Exception {
        http
            // ✅ Ensure custom CORS config is used
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/chat-widget.html"
                ).permitAll()

                // ✅ Chatbot endpoint allowed publicly
                .requestMatchers("/api/ai/chat").permitAll()

                // Other publicly accessible endpoints
                .requestMatchers(
                    "/Announcement/**",
                    "/recompenses/**",
                    "/categories/**",
                    "/notifications/**",
                    "/plannings/**",
                    "/panelists/**"
                ).permitAll()

                // ✅ Preflight CORS requests must always be allowed
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Restricted sections
                .requestMatchers("/admin/**", "/api/v1/admin/**").hasRole("ADMINVALIDATION")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        System.out.println("🔒 Security configuration loaded — Chat endpoint allowed");

        return http.build();
    }

    // ✅ Clean, safe, and correct CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
            "http://localhost:4200",
            "http://127.0.0.1:4200"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // ✅ Only call once — you had duplicate lines before
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
