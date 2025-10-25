
package com.example.evaliaproject.auth;

import com.example.evaliaproject.config.JwtService;
import com.example.evaliaproject.entity.Token;
import com.example.evaliaproject.entity.TokenType;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.TokenRepository;
import com.example.evaliaproject.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@CrossOrigin(origins ="http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class OtpLoginController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @PostMapping("/confirm-otp")
    public ResponseEntity<AuthenticationResponse> confirmOtp(@RequestBody @Validated ConfirmOtpRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Token token = tokenRepository.findByTokenAndType(req.getCode(), TokenType.LOGIN_OTP)
                .orElseThrow(() -> new IllegalArgumentException("Code invalide"));

        if (!token.getUser().getId_user().equals(user.getId_user()))
            throw new IllegalStateException("Code non associé à cet utilisateur");
        if (token.isRevoked())
            throw new IllegalStateException("Code déjà utilisé");
        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Code expiré");

        // Marquer utilisé
        token.setValidatedAt(LocalDateTime.now());
        token.setUsedAt(LocalDateTime.now());
        token.setRevoked(true);
        tokenRepository.save(token);

        // Désactiver l'obligation OTP pour la suite
//        if (user.isFirstLoginCompleted()) {
//            user.setFirstLoginCompleted(false);
//            userRepository.save(user);
//        }
        // ✅ Marquer la 1ʳᵉ connexion terminée (plus d’OTP les fois suivantes)
        if (!user.isFirstLoginCompleted()) {
            user.setFirstLoginCompleted(true);
            userRepository.save(user);
        }

        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwt)
                .message("Authentification réussie")
                .pending(false)
                .build());
    }

    @Data
    public static class ConfirmOtpRequest {
        @Email @NotBlank private String email;
        @NotBlank private String code;
    }
}
