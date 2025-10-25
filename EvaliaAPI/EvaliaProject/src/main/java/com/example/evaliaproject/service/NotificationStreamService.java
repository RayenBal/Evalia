package com.example.evaliaproject.service;

import com.example.evaliaproject.dto.NotificationDto;
import com.example.evaliaproject.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Gère les connexions SSE par utilisateur et l'envoi
 * de notifications aux clients connectés.
 */
@Service
public class NotificationStreamService {




    private final ConcurrentMap<Long, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private static final long TIMEOUT_MS = Duration.ofMinutes(30).toMillis();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitters.computeIfAbsent(userId, id -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        try { emitter.send(SseEmitter.event().name("hello").data("connected")); }
        catch (IOException ignored) {}

        return emitter;
    }

    /** ⬇️ maintenant on push un DTO */
    public void push(NotificationDto dto, Long recipientUserId) {
        Set<SseEmitter> set = emitters.get(recipientUserId);
        if (set == null || set.isEmpty()) return;

        for (SseEmitter em : set) {
            try {
                em.send(
                        SseEmitter.event()
                                .name("notification")
                                .id(dto.id())
                                .data(dto)
                );
            } catch (IOException e) {
                remove(recipientUserId, em);
            }
        }
    }

    public void ping(Long userId) {
        Set<SseEmitter> set = emitters.get(userId);
        if (set == null) return;
        for (SseEmitter em : set) {
            try { em.send(SseEmitter.event().name("ping").data("•")); }
            catch (IOException ignored) {}
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        var set = emitters.get(userId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) emitters.remove(userId);
        }
    }

}
//
//    // Map: userId -> set d'emitters (un par onglet navigateur)
//    private final ConcurrentMap<Long, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();
//
//    // Durée de vie d'une connexion SSE (le navigateur se reconnecte)
//    private static final long TIMEOUT_MS = Duration.ofMinutes(30).toMillis();
//
//    /**
//     * Appelé quand un client s'abonne au flux SSE (/notifications/stream).
//     */
//    public SseEmitter register(Long userId) {
//        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
//
//        emitters.computeIfAbsent(userId, id -> new CopyOnWriteArraySet<>()).add(emitter);
//
//        // Nettoyage quand la connection se termine/expire/erreur
//        emitter.onCompletion(() -> remove(userId, emitter));
//        emitter.onTimeout(() -> remove(userId, emitter));
//        emitter.onError(e -> remove(userId, emitter));
//
//        // message "hello" immédiat (facultatif)
//        try {
//            emitter.send(SseEmitter.event().name("hello").data("connected"));
//        } catch (IOException ignored) {}
//
//        return emitter;
//    }
//
//    /**
//     * Envoie une notification à tous les clients connectés du destinataire.
//     * Appeler ceci APRÈS COMMIT (voir NotificationService).
//     */
//    public void push(Notification n) {
//        Long uid = n.getRecipient().getId_user();
//        Set<SseEmitter> set = emitters.get(uid);
//        if (set == null || set.isEmpty()) return;
//
//        for (SseEmitter em : set) {
//            try {
//                em.send(SseEmitter.event()
//                        .name("notification")     // event name côté front
//                        .id(n.getIdnotif())       // permet "Last-Event-ID" si besoin
//                        .data(n));                // Spring convertit en JSON
//            } catch (IOException e) {
//                // si le client a fermé, on retire l'emitter
//                remove(uid, em);
//            }
//        }
//    }
//
//    /** Envoi d'un ping pour garder la connexion active (optionnel). */
//    public void ping(Long userId) {
//        Set<SseEmitter> set = emitters.get(userId);
//        if (set == null) return;
//        for (SseEmitter em : set) {
//            try { em.send(SseEmitter.event().name("ping").data("•")); }
//            catch (IOException ignored) {}
//        }
//    }
//
//    private void remove(Long userId, SseEmitter emitter) {
//        var set = emitters.get(userId);
//        if (set != null) {
//            set.remove(emitter);
//            if (set.isEmpty()) emitters.remove(userId);
//        }
//    }
//}
