package com.example.evaliaproject.controller;

import com.example.evaliaproject.auth.AuthenticationService;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

//@CrossOrigin("http://localhost:4200")
//@Controller
//@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminValidationController {
    AuthenticationService authenticationService;

    private final UserRepository userRepository;
//    @GetMapping("/pending-users")
//    @PreAuthorize("hasRole('ADMINVALIDATION')")
//    public List<User> getPendingUsers() {
//        return userRepository.findAll().stream()
//                .filter(u -> u.isVerified()
//                        && u.isNeedsAdminValidation()
//                        && !u.isEnabled())
//                .collect(Collectors.toList());
//    }
//
//
//
//    @PostMapping("/validate-user/{userId}")
//    @PreAuthorize("hasRole('ADMINVALIDATION')")
//    public ResponseEntity<String> validateUser(@PathVariable Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
//        // Si l’e-mail n’a pas été vérifié, on refuse
//        if (!user.isVerified()) {
//            return ResponseEntity.badRequest().body("L'utilisateur n'a pas encore validé son e-mail");
//        }
//        // On passe needsAdminValidation et enabled
//        user.setNeedsAdminValidation(false);
//        user.setEnabled(true);
//        userRepository.save(user);
//        return ResponseEntity.ok("Utilisateur validé par l'admin");
//    }
//
//
//
//
//


//    @PostMapping("/validate/{id}")
//    public String validateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        authenticationService.validateUserByAdmin(id);
//        redirectAttributes.addFlashAttribute("message", "Utilisateur validé et mail de vérification envoyé.");
//        return "redirect:/admin/pending-users";
//    }



    @GetMapping("/pending-users")
    public String showPendingUsers(Model model) {
        List<com.example.evaliaproject.entity.User> pendingUsers = userRepository.findAll().stream()
                .filter(u -> u.isVerified() && u.isNeedsAdminValidation() && !u.isEnabled())
                .toList();
        model.addAttribute("pendingUsers", pendingUsers);
        return "pending_users"; // page Thymeleaf
    }

    @PostMapping("/validate-user/{id}")
    public String validateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setNeedsAdminValidation(false);
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/admin/pending-users";
    }

    @PostMapping("/reject-user/{id}")
    public String rejectUser(@PathVariable Long id) {
        userRepository.deleteById(id); // ou simplement désactiver
        return "redirect:/admin/pending-users";
    }


}
