package com.example.evaliaproject.controller;

import com.example.evaliaproject.repository.UserRepository;
import com.example.evaliaproject.service.NotificationStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Endpoint SSE : le front s'y connecte via EventSource.
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class NotificationSseController {

    private final NotificationStreamService streamService;
    private final UserRepository userRepository;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        var me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));
        // Enregistre l'émetteur pour cet utilisateur
        return streamService.register(me.getId_user());
    }
}
