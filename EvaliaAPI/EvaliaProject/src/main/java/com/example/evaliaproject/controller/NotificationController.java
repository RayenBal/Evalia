package com.example.evaliaproject.controller;


import com.example.evaliaproject.dto.NotificationDto;
import com.example.evaliaproject.dto.NotificationMapper;
import com.example.evaliaproject.entity.Notification;
import com.example.evaliaproject.repository.UserRepository;
import com.example.evaliaproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Endpoints REST pour le front (Angular) :
 * - GET /notifications/me : liste des notifs de l'utilisateur courant
 * - POST /notifications/{id}/seen : marquer une notif comme vue
 * - POST /notifications/seen/all : tout marquer comme vu
 * - GET /notifications/me/unseen-count : compteur "non vues"
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public List<NotificationDto> myNotifications(Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        var me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));

        return notificationService.listForUser(me.getId_user())
                .stream().map(NotificationMapper::toDto).toList();
    }

    @PostMapping("/{id}/seen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markSeen(@PathVariable String id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        notificationService.markSeen(id);
    }

    @PostMapping("/seen/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllSeen(Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        var me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));
        notificationService.markAllSeen(me.getId_user());
    }

    @GetMapping("/me/unseen-count")
    public Map<String, Long> unseenCount(Authentication auth) {
        if (auth == null || !auth.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        var me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));
        return Map.of("count", notificationService.unseenCount(me.getId_user()));
    }
}

















//    private final NotificationService notificationService;
//    private final UserRepository userRepository;
//
//    @GetMapping("/me")
//    public List<Notification> myNotifications(Authentication auth) {
//        if (auth == null || !auth.isAuthenticated())
//            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        var me = userRepository.findByEmail(auth.getName())
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));
//        return notificationService.listForUser(me.getId_user());
//    }
//
//    @PostMapping("/{id}/seen")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void markSeen(@PathVariable String id, Authentication auth) {
//        if (auth == null || !auth.isAuthenticated())
//            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        notificationService.markSeen(id);
//    }
//
//    @PostMapping("/seen/all")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void markAllSeen(Authentication auth) {
//        if (auth == null || !auth.isAuthenticated())
//            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        var me = userRepository.findByEmail(auth.getName())
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));
//        notificationService.markAllSeen(me.getId_user());
//    }
//
//    @GetMapping("/me/unseen-count")
//    public Map<String, Long> unseenCount(Authentication auth) {
//        if (auth == null || !auth.isAuthenticated())
//            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        var me = userRepository.findByEmail(auth.getName())
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + auth.getName()));
//        return Map.of("count", notificationService.unseenCount(me.getId_user()));
//    }
//}