package com.example.evaliaproject.service;
import org.springframework.security.access.AccessDeniedException;

import com.example.evaliaproject.dto.AssignPanelistDto;
import com.example.evaliaproject.dto.SlotCreateDto;
import com.example.evaliaproject.dto.UpdatePlanningDto;
import com.example.evaliaproject.dto.UpdateStatusDto;
import com.example.evaliaproject.entity.*;
import com.example.evaliaproject.repository.AnnouncementRepository;
import com.example.evaliaproject.repository.PlanningRepository;
import com.example.evaliaproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final PlanningRepository repo;
    private final AnnouncementRepository announcementRepo;
    private final UserRepository userRepo;

    @Transactional
    public List<Planning> createSlots(Long advertiserEmail, SlotCreateDto dto) {
        User owner = userRepo.findById(advertiserEmail).orElseThrow();

        if (owner.getTypeUser() != TypeUser.Announceur)
            throw new org.springframework.security.access.AccessDeniedException("Réservé aux annonceurs");

        Announce ann = announcementRepo.findById(dto.getAnnouncementId()).orElseThrow();
        if (ann.getUser() == null || !ann.getUser().getId_user().equals(owner.getId_user()))
            throw new org.springframework.security.access.AccessDeniedException("Pas propriétaire de l’annonce");

        List<Planning> out = new ArrayList<>();
//        for (var s : dto.getSlots()) {
//            if (s.getStartsAt() == null || s.getEndsAt() == null || !s.getEndsAt().isAfter(s.getStartsAt()))
//                throw new IllegalArgumentException("Créneau invalide");
//            out.add(Planning.builder()
//                    .announcement(ann).owner(owner )
//                    .startsAt(s.getStartsAt()).endsAt(s.getEndsAt())
//                    .status(AppointmentStatus.PENDING).build());
//        }
//        return repo.saveAll(out);
        for (var s : dto.getSlots()) {
            if (s.getStartsAt() == null || s.getEndsAt() == null || !s.getEndsAt().isAfter(s.getStartsAt()))
                throw new IllegalArgumentException("Créneau invalide");

            User panel = null;
            var status = AppointmentStatus.PENDING;
            if (s.getPanelistId() != null) {
                panel = userRepo.findById(s.getPanelistId()).orElseThrow();
                if (panel.getTypeUser() != TypeUser.Paneliste)
                    throw new IllegalArgumentException("L’utilisateur assigné n’est pas paneliste");

                var conflicts = repo.findConflicts(panel.getId_user(), s.getStartsAt(), s.getEndsAt());
                if (!conflicts.isEmpty())
                    throw new IllegalStateException("Conflit pour ce paneliste");
                status = AppointmentStatus.PENDING;
            }

            out.add(Planning.builder()
                    .announcement(ann)
                    .owner(owner)
                    .panelist(panel) // ← rempli si fourni
                    .startsAt(s.getStartsAt())
                    .endsAt(s.getEndsAt())
                    //.status(panel == null ? AppointmentStatus.PENDING : AppointmentStatus.CONFIRMED)
                     .status(status)
                    .build());
        }
        return repo.saveAll(out);
    }

    @Transactional
    public Planning assignPanelist(String advertiserEmail, String appointmentId, AssignPanelistDto body) {
        User adv = userRepo.findByEmail(advertiserEmail).orElseThrow();
        Planning appt = repo.findById(appointmentId).orElseThrow();
        if (!appt.getOwner().getId_user().equals(adv.getId_user()))
            throw new org.springframework.security.access.AccessDeniedException("RDV non détenu");
        User panel = userRepo.findById(body.getPanelistId()).orElseThrow();
        if (panel.getTypeUser() != TypeUser.Paneliste)
            throw new IllegalArgumentException("L’utilisateur assigné n’est pas paneliste");

        var conflicts = repo.findConflicts(panel.getId_user(), appt.getStartsAt(), appt.getEndsAt());
        if (!conflicts.isEmpty()) throw new IllegalStateException("Conflit pour ce paneliste");

        appt.setPanelist(panel);
        if (appt.getStatus() == AppointmentStatus.PENDING) appt.setStatus(AppointmentStatus.CONFIRMED);
        return repo.save(appt);
    }
    @Transactional
    public Planning updateStatus(String actorEmail, String id, UpdateStatusDto body) {
        var actor = userRepo.findByEmail(actorEmail).orElseThrow();
        var appt  = repo.findById(id).orElseThrow();

        var isOwner  = appt.getOwner() != null    && appt.getOwner().getId_user().equals(actor.getId_user());
        var isPanel  = appt.getPanelist() != null && appt.getPanelist().getId_user().equals(actor.getId_user());

        if (!isOwner && !isPanel) throw new AccessDeniedException("Non autorisé");

        var newStatus = body.getStatus();
        if (newStatus == null) throw new IllegalArgumentException("Status manquant");

        if (isPanel && !isOwner) {
            var allowed = EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED);
            if (!allowed.contains(newStatus)) throw new AccessDeniedException("Statut non autorisé (paneliste)");
        }
        if (isOwner && !isPanel) {
            var allowed = EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);
            if (!allowed.contains(newStatus)) throw new AccessDeniedException("Statut non autorisé (owner)");
        }
        appt.setStatus(newStatus);
        return repo.save(appt);
    }

//    @Transactional
//    public Planning updateStatus(String actorEmail, String id, UpdateStatusDto body) {
//        User actor = userRepo.findByEmail(actorEmail).orElseThrow();
//        Planning appt = repo.findById(id).orElseThrow();
//
//        boolean owner = appt.getOwner() != null && appt.getOwner().getId_user().equals(actor.getId_user());
//        boolean isPanel = appt.getPanelist() != null && appt.getPanelist().getId_user().equals(actor.getId_user());
//        if (!owner && !isPanel) throw new org.springframework.security.access.AccessDeniedException("Non autorisé");
//
//        var newStatus = body.getStatus();
//        if (newStatus == null) throw new IllegalArgumentException("Status manquant");
//
//        if (isPanel && !owner) {
//            var allowed = EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED);
//            if (!allowed.contains(newStatus))
//                throw new org.springframework.security.access.AccessDeniedException("Statut non autorisé pour paneliste");
//        }
//        appt.setStatus(newStatus);
//        return repo.save(appt);
//    }

    @Transactional(readOnly = true) public List<Planning> mine(Long panelistId) { return repo.findByPanelistId(panelistId); }
    @Transactional(readOnly = true) public List<Planning> owner(Long ownerId){
        List<Planning> plannings = repo.findByOwnerId(ownerId);
        plannings.forEach(planning ->{
            planning.setAnnouncement(announcementRepo.getById(planning.getAnnouncement().getIdAnnouncement()));
            planning.setPanelist(userRepo.getById(planning.getPanelist().getId_user()));    }

        );
        return plannings;

    }
    @Transactional(readOnly = true) public List<Planning> byAnnouncement(String annId){ return repo.findByAnnouncementId(annId); }


    @Transactional
    public Planning update(String ownerEmail, String id, UpdatePlanningDto dto) {
        var owner = userRepo.findByEmail(ownerEmail).orElseThrow();
        var appt  = repo.findById(id).orElseThrow();

        if (!appt.getOwner().getId_user().equals(owner.getId_user())) {
            throw new AccessDeniedException("Annonce non détenue");
        }

        // Changement d’annonce (optionnel)
        if (dto.getAnnouncementId() != null && !dto.getAnnouncementId().isBlank()) {
            var ann = announcementRepo.findById(dto.getAnnouncementId()).orElseThrow();
            if (ann.getUser() == null || !ann.getUser().getId_user().equals(owner.getId_user())) {
                throw new AccessDeniedException("Annonce non détenue");
            }
            appt.setAnnouncement(ann);
        }

        // Changement de dates (optionnel)
        if (dto.getStartsAt() != null) appt.setStartsAt(dto.getStartsAt());
        if (dto.getEndsAt()   != null) appt.setEndsAt(dto.getEndsAt());
        if (appt.getEndsAt() == null || !appt.getEndsAt().isAfter(appt.getStartsAt())) {
            throw new IllegalArgumentException("Créneau invalide (fin <= début)");
        }

        // Changement de paneliste (optionnel)
        if (dto.getPanelistId() != null) {
            if (dto.getPanelistId() <= 0) {
                // convention : <=0 => retirer le paneliste
                appt.setPanelist(null);
                appt.setStatus(AppointmentStatus.PENDING);
            } else {
                var panel = userRepo.findById(dto.getPanelistId()).orElseThrow();
                if (panel.getTypeUser() != TypeUser.Paneliste) {
                    throw new IllegalArgumentException("L’utilisateur n’est pas paneliste");
                }
                // anti-conflits
                var conflicts = repo.findConflicts(panel.getId_user(), appt.getStartsAt(), appt.getEndsAt());
                // ignorer soi-même si jamais on garde le même ID
                conflicts.removeIf(p -> p.getId().equals(appt.getId()));
                if (!conflicts.isEmpty()) throw new IllegalStateException("Conflit pour ce paneliste");

                appt.setPanelist(panel);
                if (appt.getStatus() == AppointmentStatus.PENDING) {
                    appt.setStatus(AppointmentStatus.CONFIRMED);
                }
            }
        }

        // Changement de statut (optionnel, sinon utiliser PATCH /status)
        if (dto.getStatus() != null) {
            appt.setStatus(dto.getStatus());
        }

        return repo.save(appt);
    }

    @Transactional
    public void delete(String ownerEmail, String id) {
        var owner = userRepo.findByEmail(ownerEmail).orElseThrow();
        var appt  = repo.findById(id).orElseThrow();

        if (!appt.getOwner().getId_user().equals(owner.getId_user())) {
            throw new AccessDeniedException("Suppression interdite : RDV non détenu");
        }
        repo.delete(appt);
    }




    // PlanningService.java
    @Transactional
    public Planning updateStatusById(Long actorId, String id, UpdateStatusDto body) {
        User actor = userRepo.findById(actorId).orElseThrow();
        Planning appt = repo.findById(id).orElseThrow();

        boolean isOwner = appt.getOwner()    != null && appt.getOwner().getId_user().equals(actor.getId_user());
        boolean isPanel = appt.getPanelist() != null && appt.getPanelist().getId_user().equals(actor.getId_user());

        if (!isOwner && !isPanel) throw new AccessDeniedException("Non autorisé");

        // Interdit à l'annonceur (consultation seulement)
        if (isOwner && !isPanel) throw new AccessDeniedException("L’annonceur ne peut pas modifier le statut");

        // Ici, c’est un paneliste
        var newStatus = body.getStatus();
        if (newStatus == null) throw new IllegalArgumentException("Status manquant");
        var allowed = EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED);
        if (!allowed.contains(newStatus))
            throw new AccessDeniedException("Paneliste : statut autorisé = CONFIRMED ou CANCELLED");

        appt.setStatus(newStatus);
        return repo.save(appt);
    }
}





//    private final UserRepository userRepo;
//    private final NotificationService notificationService; // si tu veux notifier le paneliste
//
//    @Transactional(readOnly = true)
//    public List<PlanningDto> ownerList(Long ownerId) {
//        return repo.ownerPlans(ownerId).stream().map(PlanningDto::of).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<PlanningDto> panelistList(Long panelistId) {
//        return repo.panelistPlans(panelistId).stream().map(PlanningDto::of).toList();
//    }
//
//    @Transactional
//    public PlanningDto create(Long ownerId, UpsertPlanningRequest req) {
//        var owner = userRepo.findById(ownerId).orElseThrow();
//        User panel = null;
//        if (req.panelistId() != null) {
//            panel = userRepo.findById(req.panelistId()).orElseThrow();
//            // vérif chevauchement
//            var end = endOrDefault(req.startAt(), req.endAt());
//            ensureNoOverlap(null, panel.getId_user(), req.startAt(), end);
//        }
//        var p = Planning.builder()
//                .owner(owner)
//                .title(req.title())
//                .startAt(req.startAt())
//                .endAt(req.endAt())
//                .status(req.status() == null ? AppointmentStatus.PENDING : req.status())
//                .note(req.note())
//                .panelist(panel)
//                .build();
//        var saved = repo.save(p);
//
//        if (panel != null) {
//            notificationService.notify(
//                    panel, null,
//                    "Un rendez-vous « test au bureau » vous a été attribué le " + saved.getStartAt(),
//                    NotificationType.FEEDBACK_RECEIVED // ou un type dédié
//            );
//        }
//        return PlanningDto.of(saved);
//    }
//
//    @Transactional
//    public PlanningDto update(Long ownerId, String id, UpsertPlanningRequest req) {
//        var p = repo.findById(id).orElseThrow();
//        if (!p.getOwner().getId_user().equals(ownerId)) {
//            throw new org.springframework.security.access.AccessDeniedException("Not owner");
//        }
//        // si on change le paneliste
//        if (req.panelistId() != null && (p.getPanelist()==null ||
//                !p.getPanelist().getId_user().equals(req.panelistId()))) {
//            var panel = userRepo.findById(req.panelistId()).orElseThrow();
//            var end = endOrDefault(req.startAt()!=null?req.startAt():p.getStartAt(),
//                    req.endAt()!=null?req.endAt():p.getEndAt());
//            ensureNoOverlap(p.getId(), panel.getId_user(),
//                    req.startAt()!=null?req.startAt():p.getStartAt(),
//                    end);
//            p.setPanelist(panel);
//        }
//        if (req.title()!=null)    p.setTitle(req.title());
//        if (req.startAt()!=null)  p.setStartAt(req.startAt());
//        if (req.endAt()!=null)    p.setEndAt(req.endAt());
//        if (req.status()!=null)   p.setStatus(req.status());
//        if (req.note()!=null)     p.setNote(req.note());
//        return PlanningDto.of(repo.save(p));
//    }
//
//    @Transactional
//    public PlanningDto assign(Long ownerId, String id, Long panelistId) {
//        var p = repo.findById(id).orElseThrow();
//        if (!p.getOwner().getId_user().equals(ownerId))
//            throw new org.springframework.security.access.AccessDeniedException("Not owner");
//        var panel = userRepo.findById(panelistId).orElseThrow();
//        var end = endOrDefault(p.getStartAt(), p.getEndAt());
//        ensureNoOverlap(p.getId(), panel.getId_user(), p.getStartAt(), end);
//        p.setPanelist(panel);
//        p.setStatus(AppointmentStatus.CONFIRMED);
//        var saved = repo.save(p);
//
//        notificationService.notify(
//                panel, null,
//                "Un rendez-vous « test au bureau » vous a été attribué le " + saved.getStartAt(),
//                NotificationType.FEEDBACK_RECEIVED
//        );
//        return PlanningDto.of(saved);
//    }
//
//    @Transactional
//    public PlanningDto unassign(Long ownerId, String id) {
//        var p = repo.findById(id).orElseThrow();
//        if (!p.getOwner().getId_user().equals(ownerId))
//            throw new org.springframework.security.access.AccessDeniedException("Not owner");
//        p.setPanelist(null);
//        p.setStatus(AppointmentStatus.PENDING);
//        return PlanningDto.of(repo.save(p));
//    }
//
//    private OffsetDateTime endOrDefault(OffsetDateTime start, OffsetDateTime end) {
//        return end != null ? end : start.plusHours(1); // défaut: 1h
//        // ajuste selon ton métier
//    }
//
//    private void ensureNoOverlap(String planningId, Long panelistId,
//                                 OffsetDateTime start, OffsetDateTime end) {
//        if (panelistId == null) return;
//        if (repo.existsOverlapForPanelist(planningId, panelistId, start, end)) {
//            throw new IllegalArgumentException("Créneau déjà pris par ce paneliste.");
//        }
//    }
//}