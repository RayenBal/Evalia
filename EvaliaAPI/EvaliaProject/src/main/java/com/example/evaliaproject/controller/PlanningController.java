package com.example.evaliaproject.controller;

import com.example.evaliaproject.dto.*;
import com.example.evaliaproject.entity.Planning;
import com.example.evaliaproject.service.PlanningService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints CRUD + assignation.
 * On déduit l'owner (annonceur) via l'email de l'utilisateur connecté.
 * Si tu as déjà le userId dans le token, adapte selon ton SecurityContext.
 */
@RestController
@RequestMapping("/plannings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PlanningController {

    private final PlanningService service;
    private final com.example.evaliaproject.repository.UserRepository userRepo;

    // Annonceur : créer des slots (batch)
//    @PostMapping("/slots/{id}")
//    public ResponseEntity<List<Planning>> createSlots(@PathVariable Long id, @RequestBody SlotCreateDto dto) {
//      return ResponseEntity.ok(service.createSlots(id, dto));
//
//    }
    @PostMapping("/slots/{id}")
    public ResponseEntity<List<PlanningDto>> createSlots(@PathVariable Long id,
                                                         @RequestBody SlotCreateDto dto) {
        var saved = service.createSlots(id, dto);        // <- List<Planning>
        var body  = saved.stream().map(PlanningDto::of).toList(); // <- List<PlanningDto>
        return ResponseEntity.ok(body);                  // <- ResponseEntity<List<PlanningDto>>
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Planning> statusById(@PathVariable String id,
                                               @RequestParam("actorId") Long actorId,
                                               @RequestBody UpdateStatusDto body) {
        return ResponseEntity.ok(service.updateStatusById(actorId, id, body));
    }
    // Annonceur : assigner un paneliste à un slot
//    @PostMapping("/{id}/assign")
//    public ResponseEntity<Planning> assign(Authentication auth, @PathVariable String id, @RequestBody AssignPanelistDto body) {
//        return ResponseEntity.ok(service.assignPanelist(auth.getName(), id, body));
//    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<PlanningDto> assign(Authentication auth, @PathVariable String id,
                                              @RequestBody AssignPanelistDto body) {
        return ResponseEntity.ok(PlanningDto.of(
                service.assignPanelist(auth.getName(), id, body)
        ));
    }
//    @PostMapping("/{id}/assign")
//    public ResponseEntity<PlanningDto> assign(Authentication auth, @PathVariable String id,
//                                              @RequestBody AssignPanelistDto body) {
//        return ResponseEntity.ok(PlanningDto.of(service.assignPanelist(auth.getName(), id, body)));
//    }

    // Changer le statut (paneliste ou annonceur)
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<Planning> status(Authentication auth, @PathVariable String id, @RequestBody UpdateStatusDto body) {
//        return ResponseEntity.ok(service.updateStatus(auth.getName(), id, body));
//    } hedhii
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<Planning> updateStatus(Authentication auth,
//                                                 @PathVariable String id,
//                                                 @RequestBody UpdateStatusDto body) {
//        return ResponseEntity.ok(service.updateStatus(auth.getName(), id, body));
//    }
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<PlanningDto> status(Authentication auth, @PathVariable String id,
//                                              @RequestBody UpdateStatusDto body) {
//        return ResponseEntity.ok(PlanningDto.of(service.updateStatus(auth.getName(), id, body)));
//    }
    // Paneliste : mon calendrier
//    @GetMapping("/mine/{id}")
//    public ResponseEntity<List<Planning>> mine(@PathVariable Long id) {
//        return ResponseEntity.ok(service.mine(id));
//    }
    @GetMapping("/mine/{id}")
    public ResponseEntity<List<PlanningDto>> mine(@PathVariable Long id) {
        return ResponseEntity.ok(service.mine(id).stream().map(PlanningDto::of).toList());
    }

    // Annonceur : mon calendrier
//    @GetMapping("/owner/{id}")
//    public ResponseEntity<List<Planning>> owner(@PathVariable Long id) {
//        return ResponseEntity.ok(service.owner(id));
//    }

    @GetMapping("/owner/{id}")
    public ResponseEntity<List<PlanningDto>> owner(@PathVariable Long id) {
        return ResponseEntity.ok(service.owner(id).stream().map(PlanningDto::of).toList());
    }
    // Filtre par annonce (utile pour vue “par annonce”)
//    @GetMapping("/by-announcement/{annId}")
//    public ResponseEntity<List<Planning>> byAnnouncement(@PathVariable String annId) {
//        return ResponseEntity.ok(service.byAnnouncement(annId));
//    }


    @GetMapping("/by-announcement/{annId}")
    public ResponseEntity<List<PlanningDto>> byAnnouncement(@PathVariable String annId) {
        return ResponseEntity.ok(service.byAnnouncement(annId).stream().map(PlanningDto::of).toList());
    }
//    @PutMapping("/{id}")
//    public ResponseEntity<Planning> update(
//            Authentication auth,
//            @PathVariable String id,
//            @RequestBody UpdatePlanningDto body) {
//        return ResponseEntity.ok(service.update(auth.getName(), id, body));
//    }
@PutMapping("/{id}")
public ResponseEntity<PlanningDto> update(Authentication auth, @PathVariable String id,
                                          @RequestBody UpdatePlanningDto body) {
    return ResponseEntity.ok(PlanningDto.of(service.update(auth.getName(), id, body)));
}
    // Suppression (owner)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable String id) {
        service.delete(auth.getName(), id);
    }
}













//    private final PlanningService service;
//    private final com.example.evaliaproject.repository.UserRepository userRepo;
//
//    private Long currentUserId(Authentication auth) {
//        var me = userRepo.findByEmail(auth.getName())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        return me.getId_user();
//    }
//
//    /** Liste des RDV de l’annonceur (pour son calendrier) */
//    @GetMapping("/owner/me")
//    public List<PlanningDto> myOwnerPlans(Authentication auth){
//        return service.ownerList(currentUserId(auth));
//    }
//
//    /** Liste des RDV du paneliste (son calendrier personnel) */
//    @GetMapping("/panelist/me")
//    public List<PlanningDto> myPanelPlans(Authentication auth){
//        return service.panelistList(currentUserId(auth));
//    }
//
//    /** Création d’un créneau (annonceur) */
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public PlanningDto create(@RequestBody UpsertPlanningRequest req, Authentication auth){
//        return service.create(currentUserId(auth), req);
//    }
//
//    /** MAJ d’un créneau (annonceur) */
//    @PutMapping("/{id}")
//    public PlanningDto update(@PathVariable String id,
//                              @RequestBody UpsertPlanningRequest req,
//                              Authentication auth){
//        return service.update(currentUserId(auth), id, req);
//    }
//
//    /** Assigner un paneliste (annonceur) */
//    @PutMapping("/{id}/assign")
//    public PlanningDto assign(@PathVariable String id,
//                              @RequestBody AssignPanelistRequest req,
//                              Authentication auth){
//        return service.assign(currentUserId(auth), id, req.panelistId());
//    }
//
//    /** Retirer un paneliste (annonceur) */
//    @PutMapping("/{id}/unassign")
//    public PlanningDto unassign(@PathVariable String id, Authentication auth){
//        return service.unassign(currentUserId(auth), id);
//    }
//}