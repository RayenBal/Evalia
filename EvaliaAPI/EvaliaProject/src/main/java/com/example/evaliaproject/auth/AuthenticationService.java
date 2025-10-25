package com.example.evaliaproject.auth;

import com.example.evaliaproject.entity.*;
import com.example.evaliaproject.repository.RoleRepository;
import com.example.evaliaproject.service.FileStorageService;
import com.example.evaliaproject.service.ServiceEmail;
import com.example.evaliaproject.config.JwtService;
import com.example.evaliaproject.repository.TokenRepository;
import com.example.evaliaproject.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;
import com.example.evaliaproject.entity.TypeUser;
import java.io.IOException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
@RestControllerAdvice
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final ServiceEmail serviceEmail;
    private final FileStorageService fileStorageService;
//    private Token token;
    private final PasswordEncoder passwordEncoder;
    @Qualifier("authenticationManager")
    private final AuthenticationManager authenticationManager;
    private static final java.util.Set<String> ALLOWED_AGE_RANGES = java.util.Set.of("18_25", "26_35", "36_45", "46_60", "60_plus"
    );
    public AuthenticationResponse register(RegisterRequest request, MultipartFile registreCommerce) {
        final boolean isPaneliste = request.getTypeUser() == TypeUser.Paneliste;

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .numTelephone(trimOrNull(request.getNumTelephone()))
                .typeUser(request.getTypeUser())
                .companyName(isPaneliste ? null : trimOrNull(request.getCompanyName()))
                .jobTitle(isPaneliste ? trimOrNull(request.getJobTitle()) : null)
                .ageRange(request.getAgeRange() == null ? null : request.getAgeRange().trim())

                .deliveryAddress(request.getDeliveryAddress())
                .verified(false)
                .enabled(false)
                .needsAdminValidation(true)
                .firstLoginCompleted(false)
                .status(UserStatus.PENDING)
                .build();
        user.setIban(request.getIban());
        // 🔴 OBLIGATOIRE pour Announceur : PDF de registre de commerce
        if (request.getTypeUser() == TypeUser.Announceur) {
            try {
                if (registreCommerce == null || registreCommerce.isEmpty()) {
                    // message tel que demandé
                    throw new IllegalArgumentException("obligation de copie de registre de commerce");
                }
                String relativePath = fileStorageService.savePdf(
                        registreCommerce,
                        "registre",
                        "user-" + request.getEmail().replaceAll("[^a-zA-Z0-9]", "_")
                );
                user.setRegistreCommercePath(relativePath);
                user.setRegistreCommerceOriginalName(registreCommerce.getOriginalFilename());
            } catch (IllegalArgumentException e) {
                throw e; // "obligation de copie..." ou "Le fichier doit être un PDF."
            } catch (IOException io) {
                throw new RuntimeException("Impossible d'enregistrer le fichier de registre de commerce");
            }
        }

        userRepository.save(user);

        // Code d’activation + e-mail
        String activationCode = generateActivationTokenForUser(user);
        user.setActivationCode(activationCode);
        userRepository.save(user);

        String verificationLink = "http://localhost:8081/api/v1/auth/verify?code=" + activationCode;
        serviceEmail.sendVerificationEmail(user.getEmail(), verificationLink);

        return AuthenticationResponse.builder()
                .token("")
                .message("Inscription réussie. Votre compte est en attente de validation par l’administrateur, verifier votre mail  pour finaliser la création de votre compte")
                .pending(true)
                .build();
    }


    //    public AuthenticationResponse register(RegisterRequest request) {
//        final boolean isPaneliste = request.getTypeUser() == TypeUser.Paneliste;
//
//
//        var user = User.builder()
//                .firstname(request.getFirstname())
//                .lastname(request.getLastname())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .numTelephone(trimOrNull(request.getNumTelephone()))
//                .typeUser(request.getTypeUser())
//                .companyName(isPaneliste ? null : trimOrNull(request.getCompanyName()))
//                .jobTitle(isPaneliste ? trimOrNull(request.getJobTitle()) : null)
//
//                .ageRange(request.getAgeRange().trim())
//
//                .verified(false)
//                .enabled(false)
//                .needsAdminValidation(true)
//                .firstLoginCompleted(false)
//                .status(UserStatus.PENDING)
//                .build();
//        userRepository.save(user);
//        String activationCode = generateActivationTokenForUser(user); // génère et enregistre le token lié au user
//        user.setActivationCode(activationCode);
//        userRepository.save(user);
//
//        // 2) ENVOI D’EMAIL ICI ✅ (dev: http, prod: https + ton domaine)
//        String verificationLink = "http://localhost:8081/api/v1/auth/verify?code=" + activationCode;
//        serviceEmail.sendVerificationEmail(user.getEmail(), verificationLink);
////
////
//
//        return AuthenticationResponse.builder()
//                .token("")   // PAS DE JWT pour l’instant
//                .message("Inscription réussie. Votre compte est en attente de validation par l’administrateur.")
//                .pending(true)
//                .build();
//
//    }
    private static String trimOrNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    @Transactional
    public void verifyEmail(String code)  {
      //  Token token = tokenRepository.findByToken(code)
        Token token = tokenRepository.findByTokenAndType(code, TokenType.VERIFY_EMAIL)
                .orElseThrow(() -> new IllegalArgumentException("Code invalide"));

        // 🛑 Vérifier si déjà utilisé
        if (token.isRevoked()) {
            throw new IllegalStateException("Ce lien de vérification a déjà été utilisé.");
        }


//        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new IllegalStateException("Code d’activation expiré");
//        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            // 👉 Le token est expiré → on en génère un nouveau
            String newCode = generateActivationTokenForUser(token.getUser());
            serviceEmail.sendVerificationEmail(
                    token.getUser().getEmail(),
                    "https://<ton-domaine>/api/v1/auth/verify?code=" + newCode
            );
            throw new IllegalStateException("Ce lien a expiré. Un nouvel e-mail a été envoyé à " + token.getUser().getEmail());
        }
        User user = token.getUser();
        user.setVerified(true);
        userRepository.save(user);

        token.setValidatedAt(LocalDateTime.now());
        token.setUsedAt(LocalDateTime.now());
        token.setRevoked(true);

        tokenRepository.save(token);
        // Optionnel : supprimer le token ou le laisser pour historique
        // tokenRepository.delete(token);

    }


//private void sendValidationEmail(User user){
//        var newToken= genetrateAndSaveActivationToken(user);
//}
//
//    private String genetrateAndSaveActivationToken(User user) {
//        String generatedToken= genetrateActivationCode(6);
//        var token= Token.builder()
//                .token(generatedToken)
//                .createdAt(LocalDateTime.now())
//                .expiresAt(LocalDateTime.now().plusHours(2))
//                .user(user)
//                .build();
//        tokenRepository.save(token);
//        //
//        return generatedToken;
//    }
//
//    private String genetrateActivationCode(int length) {
//        String characters="0123456789";
//        StringBuilder codeBuilder= new StringBuilder();
//        SecureRandom secureRandam=new SecureRandom();
//        for(int i=0;i<length;i++){
//            int randomIndex= secureRandam.nextInt(characters.length());
//            codeBuilder.append(characters.charAt(randomIndex));
//
//        }
//        return codeBuilder.toString();
//    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1) Charger l’utilisateur par email (pas de fuite d’info si inconnu)
        var opt = userRepository.findByEmail(request.getEmail());
        if (opt.isEmpty()) {
            throw new org.springframework.security.authentication.BadCredentialsException("Email ou mot de passe incorrect.");
        }
        var user = opt.get();

        // 2) Etats métier AVANT test du mot de passe
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Votre compte a été rejeté par l’administrateur."
            );
        }
        if (user.isNeedsAdminValidation()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Verifier votre mail  pour finaliser la création de votre compte ,votre compte n’est pas encore validé par l’administrateur."
            );
        }
        if (!user.isVerified()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Veuillez d’abord vérifier votre e-mail."
            );
        }
        if (!user.isEnabled()) { // sécurité au cas où
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Votre compte est désactivé."
            );
        }

        // 3) Vérifier le mot de passe (à la main)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Email ou mot de passe incorrect.");
        }

        // 4) Première connexion → OTP
        if (!Boolean.TRUE.equals(user.isFirstLoginCompleted())) {
            sendFirstLoginOtp(user);
            return AuthenticationResponse.builder()
                    .token("")
                    .message("Code OTP envoyé à votre e-mail.")
                    .pending(true)
                    .build();
        }

        // 5) OK → JWT
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .message("Authentification réussie")
                .pending(false)
                .build();
    }


    @Transactional
    public void resendVerificationFor(User user) {
        String code = generateActivationTokenForUser(user); // ta méthode existante
        String verificationLink = "http://localhost:8081/api/v1/auth/verify?code=" + code;
        serviceEmail.sendVerificationEmail(user.getEmail(), verificationLink);
    }


//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//        // 2) Charger l’utilisateur
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));
//
//        // 3) Vérification des flags
//        if (!user.isVerified()) {
//            throw new IllegalStateException("Compte non vérifié via e-mail");
//        }
//        if (user.isNeedsAdminValidation()) {
//            throw new IllegalStateException("Compte en attente de validation administrateur");
//        }
//        // 2) Bloquer si désactivé / rejeté
//        if (!user.isEnabled()|| user.getStatus() == UserStatus.REJECTED) {
//            throw new DisabledException("User is rejected/disabled");
//        }
////        if (!user.isEnabled()) {
////            throw new IllegalStateException("Compte désactivé");
////        }
//
//
//        // ⬇️ Première connexion → envoi OTP + réponse "pending"
//        if (!Boolean.TRUE.equals(user.isFirstLoginCompleted())) {
//            sendFirstLoginOtp(user);
//            return AuthenticationResponse.builder()
//                    .token("")                  // pas de JWT maintenant
//                    .message("Code OTP envoyé à votre e-mail.")
//                    .pending(true)              // ⚠️ front doit afficher le champ "code"
//                    .build();
//        }
//
//
//
//        // 4) Générer et renvoyer le JWT
//        String token = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(token)
//                .message("Authentification réussie")
//                .pending(false)
//                .build();
//    }











    /**
     * crée un Token d’activation (validité 2 heures) pour ce user en base,
     * et renvoie la chaîne aléatoire à 6 chiffres.
     */
    private String generateActivationTokenForUser(User user) {
        String generatedCode = generateRandomCode(6);

        // Révoquer tous les anciens tokens du user
        List<Token> oldTokens = tokenRepository.findAllByUser(user);
        for (Token oldToken : oldTokens) {
            oldToken.setRevoked(true);
        }
        tokenRepository.saveAll(oldTokens);

        Token token = Token.builder()
                .token(generatedCode)
                .type(TokenType.VERIFY_EMAIL)           // ✅ IMPORTANT
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(8))
                .revoked(false)
                .user(user)
                .build();

        tokenRepository.save(token);
        // ✅ Sauvegarder aussi dans la table User
        user.setActivationCode(generatedCode);
        userRepository.save(user);

        return generatedCode;

    }

//        var user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow();
//        var jwtToken=jwtService.generateToken(user);
//
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
private String generateRandomCode(int length) {
    String chars = "0123456789";
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
        int idx = random.nextInt(chars.length());
        sb.append(chars.charAt(idx));
    }
    return sb.toString();
}
    private void sendFirstLoginOtp(User user) {
        // Révoquer anciens OTP LOGIN
        var all = tokenRepository.findAllByUser(user);
        all.stream()
                .filter(t -> t.getType() == TokenType.LOGIN_OTP && !t.isRevoked())
                .forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(all);

        String code = generateRandomCode(6);
        var token = Token.builder()
                .token(code)
                .type(TokenType.LOGIN_OTP)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);

        serviceEmail.sendEmail(
                user.getEmail(),
                "Votre code de première connexion",
                "Voici votre code de connexion : " + code + "\n(valide 10 minutes)."
        );
    }

//    public void validateUserByAdmin(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
//
//        if (!user.isNeedsAdminValidation()) {
//            throw new RuntimeException("L'utilisateur a déjà été validé.");
//        }
//
//        user.setNeedsAdminValidation(false);
//        userRepository.save(user);
//
//        String activationCode = generateActivationTokenForUser(user);
//        String verificationLink = "http://localhost:8081/api/v1/auth/verify?code=" + activationCode;
//
//        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
//    }




}
