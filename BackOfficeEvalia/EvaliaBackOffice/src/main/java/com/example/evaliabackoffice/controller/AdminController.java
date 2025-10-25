package com.example.evaliabackoffice.controller;


import com.example.evaliabackoffice.dto.PendingUserDTO;
import com.example.evaliabackoffice.entity.Role;
import com.example.evaliabackoffice.entity.User;
import com.example.evaliabackoffice.repository.AdminRepository;
import com.example.evaliabackoffice.repository.RoleRepository;
import com.example.evaliabackoffice.repository.UserRepository;
import com.example.evaliabackoffice.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.ui.Model;
import com.example.evaliabackoffice.entity.Admin;
import com.example.evaliabackoffice.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("admin")
@CrossOrigin("http://localhost:4200")
public class AdminController {



    @Autowired
    private EmailService emailService;
    @Autowired
    IAdminService iAdminService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AdminRepository adminRepository;
    @Autowired
    UserRepository userRepository;
    public String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    @GetMapping("/debug-users")
    @ResponseBody
    public String debugUsers() {
        return userRepository.findAll().toString();
    }
//    @GetMapping("/login")
//    public String loginPage() {
//        return "login"; // It should resolve to templates/login.html
//    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("admin", new Admin());
        model.addAttribute("rolesList", roleRepository.findAll());
        return "register"; // correspond à templates/register.html
    }

    @PostMapping("/register")
    public String registerAdmin(@ModelAttribute("admin") Admin admin) {

        if (admin.getRoles() == null) {
            admin.setRoles(new ArrayList<>());
        }
        String rawPassword = admin.getConfirmPassword();
        iAdminService.addAdmin(admin);
        try {
            String subject = "Welcome to Evalia - Your Account Details";
            String body = "Hello " + admin.getFirstname() + "," +
                    "Your account has been created successfully." +
                    "Email: " + admin.getEmail() + "" +
                    "Password: " + rawPassword + "" +
                    "Please log in .";

            emailService.sendEmail(admin.getEmail(), subject, body);

        } catch (Exception e) {
            e.printStackTrace(); // You can also log this properly
        }
        return "redirect:/admin/list";
    }

    @GetMapping("/list")
    public String showAdminList(Model model) {
        model.addAttribute("admins", iAdminService.getAllAdmins());
        return "admin_list";
    }

//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable("id") Long id, Model model) {
//        Admin admin = iAdminService.getAdminById(id);
//        model.addAttribute("admin", admin);
//        model.addAttribute("rolesList", roleRepository.findAll());
//        return "edit_admin";
//    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role non trouvée : " + id));
        List<Role> allRoles = roleRepository.findAll();

        model.addAttribute("admin", admin);
        model.addAttribute("allRoles", allRoles);
        return "edit_admin";
    }


    @PostMapping("/update")
    public String updateAdmin(@ModelAttribute Admin admin,
                              @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        if (roleIds != null) {
            List<Role> selectedRoles = roleRepository.findAllById(roleIds);
            admin.setRoles(selectedRoles);
        } else {
            admin.setRoles(new ArrayList<>());
        }

        adminRepository.save(admin);
        return "redirect:/admin/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteAdmin(@PathVariable("id") Long id) {
        iAdminService.deleteAdmin(id);
        return "redirect:/admin/list";
    }
    @GetMapping("/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public String detailsAdmin(@PathVariable("id") Long id, Model model) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        model.addAttribute("admin", admin);
        return "details_admin";
    }


    // Regular Admin - Edit Profile (no role update)
@GetMapping("/profile/edit/{id}")
@PreAuthorize("isAuthenticated()")
public String showEditFormProfile( @PathVariable("id") Long id,Model model, Principal principal) {
    Admin admin = adminRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
    model.addAttribute("admin", admin);
    return "edit_profile_admin"; // Your HTML
}


    @PostMapping("/profile/update")
    public String updateAdminProfile(@ModelAttribute Admin admin) {
        Admin existingAdmin = adminRepository.findById(admin.getIdadmin())
                .orElseThrow(() -> new IllegalArgumentException("Admin non trouvé : " + admin.getIdadmin()));

        existingAdmin.setFirstname(admin.getFirstname());
        existingAdmin.setLastname(admin.getLastname());
        existingAdmin.setEmail(admin.getEmail());

        adminRepository.save(existingAdmin);

        return "redirect:/admin/details/" + admin.getIdadmin();
    }



    @GetMapping("/me")
    public String me(Principal principal) {
        Admin admin = adminRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        return "redirect:/admin/details/" + admin.getIdadmin();
    }


//    @GetMapping("/pending-accounts")
//    public List<User> getPendingAccounts() {
//        return .findUsersNeedingValidation();
//    }
//
//    @PostMapping("/validate-account/{userId}")
//    public ResponseEntity<String> validateAccount(@PathVariable Long userId) {
//        User user = userService.findById(userId);
//        String activationCode = generateActivationCode(6);
//
//        user.setActivationCode(activationCode);
//        userService.save(user);
//
//        emailService.sendActivationEmail(user.getEmail(), activationCode);
//
//        return ResponseEntity.ok("Validation email sent to client");
//    }
//@GetMapping("/pending-users")
//public String showPendingUsers(Model model) {
//  ***  List<PendingUserDTO> pendingUsers = userRepository.findAll().stream()

//    List<PendingUserDTO> pendingUsers = userRepository.findByEnabledFalseAndNeedsAdminValidationTrue()
//            .stream()
//         **   .filter(u -> u.isNeedsAdminValidation() || !u.isEnabled()) // garde tout le monde
//    List<PendingUserDTO> allUsers = userRepository.findAll()
//            .stream()
//            .map(PendingUserDTO::new)
//            .toList();
//            .filter(u -> u.isVerified() && u.isNeedsAdminValidation() && !u.isEnabled())
//            .filter(u -> !u.isVerified() && u.isNeedsAdminValidation() && !u.isEnabled())
//            .toList();
//    model.addAttribute("pendingUsers", allUsers);
//    return "pending_users"; // page Thymeleaf
//}

//    @PostMapping("/validate-user/{id}")
//    public String validateUser(@PathVariable Long id , RedirectAttributes redirectAttributes) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        if (!user.isNeedsAdminValidation() || user.isEnabled()) {
//            redirectAttributes.addFlashAttribute("errorMessage", "L'utilisateur a déjà été traité.");
//            return "redirect:/admin/pending-users";
//        }
//        user.setNeedsAdminValidation(false);
//        user.setEnabled(true);
//        userRepository.save(user);
//        userRepository.flush();
//
//
//
//
//        // Envoi du mail avec le lien de vérification
//        String verificationLink = "http://localhost:8081/api/v1/auth/verify?code=" + user.getActivationCode();
//        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
//
//        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur validé avec succès et email envoyé.");
//        return "redirect:/admin/pending-users";
//    }

//    @PostMapping("/reject-user/{id}")
//    public String rejectUser(@PathVariable Long id,  RedirectAttributes redirectAttributes) {
//        userRepository.deleteById(id); // ou simplement désactiver
//        redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur rejeté et supprimé.");
//
//        return "redirect:/admin/pending-users";
//    }
//@PostMapping("/reject-user/{id}")
//public String rejectUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//    User user = userRepository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//    user.setEnabled(false); // ne pas supprimer
//    user.setNeedsAdminValidation(false); // on ne veut plus qu’il apparaisse comme en attente
//    userRepository.save(user);
//
//    redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur rejeté avec succès.");
//    return "redirect:/admin/pending-users";
//}

//    @PostMapping("/reject-user/{id}")
//    public String rejectUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        if (!user.isNeedsAdminValidation() || user.isEnabled()) {
//            redirectAttributes.addFlashAttribute("errorMessage", "L'utilisateur a déjà été traité.");
//            return "redirect:/admin/pending-users";
//        }
//
//        user.setEnabled(false);
//        user.setNeedsAdminValidation(false);
//        userRepository.save(user);
//
//        try {
//            emailService.sendRejectionEmail(user.getEmail(), user.getFirstname());
//        } catch (MessagingException e) {
//            e.printStackTrace(); // tu peux logger proprement ici
//            redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur rejeté mais erreur lors de l'envoi du mail.");
//            return "redirect:/admin/pending-users";
//        }
//
//        redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur rejeté avec succès et mail envoyé.");
//        return "redirect:/admin/pending-users";
//    }
//


}
