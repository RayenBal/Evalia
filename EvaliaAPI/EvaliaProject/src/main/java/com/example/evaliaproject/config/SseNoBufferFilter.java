package com.example.evaliaproject.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Désactive le buffering proxy pour les réponses SSE afin
 * d'éviter des retards d'affichage côté client.
 */
@Component
@Order(1)
public class SseNoBufferFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (req.getRequestURI().startsWith("/notifications/stream")) {
            res.setHeader("X-Accel-Buffering", "no"); // Nginx
            res.setHeader("Cache-Control", "no-cache");
            res.setHeader("Connection", "keep-alive");
        }
        chain.doFilter(req, res);
    }
}
