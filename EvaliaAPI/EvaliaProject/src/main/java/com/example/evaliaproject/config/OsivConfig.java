package com.example.evaliaproject.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

@Configuration
public class OsivConfig {
    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter() {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                String uri = request.getRequestURI();
                // Pas d’OSIV sur le flux SSE :
                return uri != null && uri.startsWith("/notifications/stream");
            }
        };
    }
}