//package com.example.evaliabackoffice.controller;
//
//import com.example.evaliabackoffice.entity.User;
//import com.example.evaliabackoffice.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/admin")
//@RequiredArgsConstructor
//@CrossOrigin("http://localhost:4200") // facultatif
//public class AdminValidationController {
//
//    private final UserRepository userRepository;
//
//    /** Page liste des comptes à valider */
//    @GetMapping("/pending-users")
//    public String showPendingUsers(Model model) {
//        List<User> pendingUsers = userRepository.findAll().stream()
//                .filter(u -> Boolean.TRUE.equals(u.isVerified()))
//                .filter(User::isNeedsAdminValidation)
//                .filter(u -> !Boolean.TRUE.equals(u.isEnabled()))
//                .toList();
//        model.addAttribute("pendingUsers", pendingUsers);
//        return "pending_users";
//    }
//
//    /** Valider & activer un utilisateur */
//    @PostMapping("/validate-user/{id}")
//    public String validateUser(@PathVariable Long id, RedirectAttributes ra) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (!user.isVerified()) {
//            ra.addFlashAttribute("error", "L'utilisateur n'a pas encore validé son e-mail.");
//            return "redirect:/admin/pending-users";
//        }
//
//        user.setNeedsAdminValidation(false);
//        user.setEnabled(true);
//        userRepository.save(user);
//        ra.addFlashAttribute("success", "Utilisateur validé et activé.");
//        return "redirect:/admin/pending-users";
//    }
//
//    /** Rejeter/Supprimer un utilisateur */
//    @PostMapping("/reject-user/{id}")
//    public String rejectUser(@PathVariable Long id, RedirectAttributes ra) {
//        if (!userRepository.existsById(id)) {
//            ra.addFlashAttribute("error", "Utilisateur introuvable.");
//            return "redirect:/admin/pending-users";
//        }
//        userRepository.deleteById(id);
//        ra.addFlashAttribute("success", "Utilisateur supprimé.");
//        return "redirect:/admin/pending-users";
//    }
//}
package com.example.evaliabackoffice.controller;

import com.example.evaliabackoffice.entity.User;
import com.example.evaliabackoffice.entity.UserStatus;
import com.example.evaliabackoffice.repository.UserRepository;
import com.example.evaliabackoffice.service.AdminValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/adminn")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200") // facultatif
public class AdminValidationController {
    private final AdminValidationService adminValidationService;
    private final UserRepository userRepository;
    @Value("${upload.dir:uploads}")
    private String uploadDir;
    /** Page liste des comptes à valider */
    @GetMapping("/pending-users")
    public String showPendingUsers(Model model) {
        List<User> pendingUsers = userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.isVerified()))
                .filter(User::isNeedsAdminValidation)
                .filter(u -> !Boolean.TRUE.equals(u.isEnabled()))
                .toList();
        model.addAttribute("title", "Comptes en attente de validation");
        model.addAttribute("pendingUsers", pendingUsers);
        return "pending_users";
//        return "users_list";
    }

//    /** Valider & activer un utilisateur */
//    @PostMapping("/validate-user/{id}")
//    public String validateUser(@PathVariable Long id, RedirectAttributes ra) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (!user.isVerified()) {
//            ra.addFlashAttribute("error", "L'utilisateur n'a pas encore validé son e-mail.");
//            return "redirect:/adminn/pending-users";
//        }
//
//        user.setNeedsAdminValidation(false);
//        user.setEnabled(true);
//        user.setStatus(UserStatus.APPROVED);
//        userRepository.save(user);
//        ra.addFlashAttribute("success", "Utilisateur validé et activé.");
//        return "redirect:/adminn/pending-users";
//    }
    @PostMapping("/validate-user/{id}")
    public String validateUser(@PathVariable Long id, RedirectAttributes ra) {
        // 👉 centralise la logique + envoi de l'email dans le service
        adminValidationService.approveUser(id);
        ra.addFlashAttribute("success", "Utilisateur validé et e-mail envoyé.");
        return "redirect:/adminn/pending-users";
    }


//    /** Rejeter/Supprimer un utilisateur */
//    @PostMapping("/reject-user/{id}")
//    public String rejectUser(@PathVariable Long id, RedirectAttributes ra) {
//        if (!userRepository.existsById(id)) {
//            ra.addFlashAttribute("error", "Utilisateur introuvable.");
//            return "redirect:/adminn/pending-users";
//        }
//        userRepository.deleteById(id);
//        ra.addFlashAttribute("success", "Utilisateur supprimé.");
//        return "redirect:/adminn/pending-users";
//    }

//    /** Rejeter (désactiver) un utilisateur */
//    @PostMapping("/reject-user/{id}")
//    public String rejectUser(@PathVariable Long id, RedirectAttributes ra) {
//        if (!userRepository.existsById(id)) {
//            ra.addFlashAttribute("error", "Utilisateur introuvable.");
//            return "redirect:/adminn/pending-users";
//        }
//        adminValidationService.rejectUser(id);
//        ra.addFlashAttribute("success", "Utilisateur rejeté (désactivé) et tokens invalidés.");
//        return "redirect:/adminn/pending-users";
//    }
    @PostMapping("/reject-user/{id}")
    public String rejectUser(@PathVariable Long id) {
        adminValidationService.rejectUser(id);
        return "redirect:/adminn/pending-users";
    }

    // ► Validés
    @GetMapping("/approved-users")
    public String showApprovedUsers(Model model) {
        List<User> approved = userRepository
                .findAllByStatusOrderByCreatedDateDesc(UserStatus.APPROVED);
        model.addAttribute("title", "Utilisateurs validés");
        model.addAttribute("users", approved);
        return "users_list";
    }

    // ► Rejetés
    @GetMapping("/rejected-users")
    public String showRejectedUsers(Model model) {
        List<User> rejected = userRepository
                .findAllByStatusOrderByCreatedDateDesc(UserStatus.REJECTED);
        model.addAttribute("title", "Utilisateurs rejetés");
        model.addAttribute("users", rejected);
        return "users_list";
    }



//    @GetMapping("/admin/registre/{userId}")
//    public ResponseEntity<Resource> download(@PathVariable Long userId) throws IOException {
//        var user = userRepository.findById(userId).orElseThrow();
//        if (user.getRegistreCommercePath() == null) return ResponseEntity.notFound().build();
//        var file = java.nio.file.Paths.get("${upload.dir}", user.getRegistreCommercePath()).toAbsolutePath();
//        var res = new org.springframework.core.io.FileSystemResource(file);
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "inline; filename=\"" + (user.getRegistreCommerceOriginalName()==null?"registre.pdf":user.getRegistreCommerceOriginalName()) + "\"")
//                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
//                .body(res);
//    }
    /** Ouvrir/télécharger le registre de commerce en PDF pour un user */
//    @GetMapping("/registre/{userId}")
//    public ResponseEntity<Resource> downloadRegistre(@PathVariable Long userId) throws IOException {
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (user.getRegistreCommercePath() == null || user.getRegistreCommercePath().isBlank()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun registre de commerce pour cet utilisateur");
//        }
//
//        Path absolute = Paths.get(uploadDir).resolve(user.getRegistreCommercePath()).normalize().toAbsolutePath();
//        if (!Files.exists(absolute)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable sur le disque");
//        }
//
//        Resource resource = new FileSystemResource(absolute);
//        String filename = (user.getRegistreCommerceOriginalName() != null && !user.getRegistreCommerceOriginalName().isBlank())
//                ? user.getRegistreCommerceOriginalName()
//                : "registre-" + user.getId_user() + ".pdf";
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                // "inline" = aperçu dans l'onglet ; remplace par "attachment" si tu veux forcer le téléchargement
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename.replace("\"","") + "\"")
//                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=60")
//                .body(resource);
//    }



    @GetMapping("/registre/{userId}/download")
    public ResponseEntity<?> download(@PathVariable Long userId) throws IOException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var stored = user.getRegistreCommercePath();
        if (stored == null || stored.isBlank()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Path p = Paths.get(stored);
        Path abs = p.isAbsolute() ? p.normalize() : Paths.get(uploadDir).resolve(p).normalize().toAbsolutePath();

        if (Files.exists(abs)) {
            var res = new FileSystemResource(abs);
            String filename = (user.getRegistreCommerceOriginalName() != null && !user.getRegistreCommerceOriginalName().isBlank())
                    ? user.getRegistreCommerceOriginalName()
                    : "registre-" + user.getId_user() + ".pdf";
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename.replace("\"","") + "\"")
                    .body(res);
        }

        // fallback -> API 8081
        String url = "http://localhost:8081/api/v1/files/registre/" + userId + "/download";
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, url).build();
    }}

//    @GetMapping("/registre/{userId}")
//    public ResponseEntity<Resource> downloadRegistre(@PathVariable Long userId) throws IOException {
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        var stored = user.getRegistreCommercePath();
//        if (stored == null || stored.isBlank()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun registre de commerce pour cet utilisateur");
//        }
//
//        Path candidate = Paths.get(stored);
//        Path absolute = candidate.isAbsolute()
//                ? candidate.normalize()
//                // ❌ Paths.get(upload.dir)
//                // ✅ utilise bien la variable injectée
//                : Paths.get(uploadDir).resolve(candidate).normalize().toAbsolutePath();
//
//        System.out.println("[DOWNLOAD REGISTRE] upload.dir=" + uploadDir +
//                " | stored=" + stored + " | resolved=" + absolute);
//
//        if (!Files.exists(absolute)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable sur le disque");
//        }
//
//        Resource resource = new FileSystemResource(absolute);
//        String filename = (user.getRegistreCommerceOriginalName() != null && !user.getRegistreCommerceOriginalName().isBlank())
//                ? user.getRegistreCommerceOriginalName()
//                : "registre-" + user.getId_user() + ".pdf";
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename.replace("\"","") + "\"")
//                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=60")
//                .body(resource);
//    }
//}


