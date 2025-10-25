package com.example.evaliaproject.service;


import com.example.evaliaproject.dto.NotificationDto;
import com.example.evaliaproject.dto.NotificationMapper;
import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Notification;
import com.example.evaliaproject.entity.NotificationType;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * Service applicatif pour créer/lire/mettre à jour les notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository repo;
    private final ApplicationEventPublisher events;
    private final NotificationStreamService streamService;

    /** Event interne pour déclencher le push SSE après commit. */
    public record NotificationCreatedEvent(Notification notif) {}

    @Transactional
    public void notify(User recipient, Announce ann, String message, NotificationType type) {
        Notification n = Notification.builder()
                .recipient(recipient)
                .announcement(ann)
                .type(type)
                .message(message)
                .seen(false)
                .build();
        n = repo.save(n);
        events.publishEvent(new NotificationCreatedEvent(n)); // push sera AFTER_COMMIT
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(NotificationCreatedEvent ev) {
        Notification n = ev.notif();
        NotificationDto dto = NotificationMapper.toDto(n);
        Long uid = n.getRecipient().getId_user();
        streamService.push(dto, uid);
    }

    @Transactional(readOnly = true)
    public List<Notification> listForUser(Long userId) {
        return repo.findByRecipientOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markSeen(String id) {
        var n = repo.findById(id).orElseThrow();
        n.setSeen(true);
        repo.save(n);
    }

    @Transactional
    public void markAllSeen(Long userId) {
        var list = repo.findByRecipientOrderByCreatedAtDesc(userId);
        for (var n : list) if (!n.isSeen()) n.setSeen(true);
        repo.saveAll(list);
    }

    @Transactional(readOnly = true)
    public long unseenCount(Long userId) {
        return repo.countUnseen(userId);
    }

}

//    private final NotificationRepository repo;
//    @Autowired
//    private NotificationStreamService streamService; // 👈 SSE
//
//    /**
//     * Crée et persiste une notification, puis la pousse en SSE
//     * uniquement après COMMIT pour éviter les "fantômes".
//     */
//    @Transactional
//    public void notify(User recipient, Announce ann, String message, NotificationType type) {
//        var n = Notification.builder()
//                .recipient(recipient)
//                .announcement(ann)
//                .type(type)
//                .message(message)
//                .seen(false)
//                .build();
//        final Notification notif = n;
//        n = repo.save(n);
//
//        // Push APRÈS COMMIT (si la transaction rollback, rien n'est envoyé)
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override public void afterCommit() {
//                streamService.push(notif);
//            }
//        });
//    }
//
//    @Transactional(readOnly = true)
//    public List<Notification> listForUser(Long userId) {
//        return repo.findByRecipientOrderByCreatedAtDesc(userId);
//    }
//
//    @Transactional
//    public void markSeen(String id) {
//        var n = repo.findById(id).orElseThrow();
//        n.setSeen(true);
//        repo.save(n);
//    }
//
//    @Transactional
//    public void markAllSeen(Long userId) {
//        var list = repo.findByRecipientOrderByCreatedAtDesc(userId);
//        for (var n : list) {
//            if (!n.isSeen()) n.setSeen(true);
//        }
//        repo.saveAll(list);
//    }
//
//    @Transactional(readOnly = true)
//    public long unseenCount(Long userId) {
//        return repo.countUnseen(userId);
//    }
//}