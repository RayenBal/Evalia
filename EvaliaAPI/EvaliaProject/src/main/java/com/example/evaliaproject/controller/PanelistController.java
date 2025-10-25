package com.example.evaliaproject.controller;

import com.example.evaliaproject.dto.PanelistOptionDto;
import com.example.evaliaproject.entity.TypeUser;
import com.example.evaliaproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/panelists")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class PanelistController {

    private final UserRepository userRepo;

    // Simple : tous les utilisateurs de type Paneliste
    @GetMapping("/eligible")
    public List<PanelistOptionDto> eligible(@RequestParam(name = "announcementId", required = false) String annId) {
        return userRepo.findAll().stream()
                .filter(u -> u.getTypeUser() == TypeUser.Paneliste)
                .map(u -> new PanelistOptionDto(u.getId_user(), u.fullName(), u.getEmail()))
                .toList();
    }
}