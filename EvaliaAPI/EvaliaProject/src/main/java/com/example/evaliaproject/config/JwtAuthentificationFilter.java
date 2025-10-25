package com.example.evaliaproject.config;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component

public class JwtAuthentificationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    public JwtAuthentificationFilter(@Lazy UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }
    
    @Override
    protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain)
            throws ServletException, IOException {
//        if (request.getServletPath().contains("/api/v1/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }




        String path = request.getServletPath();
        if (path.startsWith("/api/v1/auth")|| path.startsWith("/api/ai/")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");// to pass the jwt authentification token within the header called authorization so we try to extract this header

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;

        }
        final String jwt= authHeader.substring(7);
        //final String userEmail= jwtService.extractUsername(jwt);
//        jwt = authHeader.substring(7);//extract the token from the authentification header

        String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (JwtException | IllegalArgumentException ex) {
            // Token expiré ou illisible -> on n’authentifie pas mais on NE bloque PAS la requête
            filterChain.doFilter(request, response);
            return;
        }





//        userEmail = jwtService.extractUsername(jwt);// to do extract the user email from jwt token
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);// update SecurityContextHolder
            }
        }
        filterChain.doFilter(request, response);
    }
}