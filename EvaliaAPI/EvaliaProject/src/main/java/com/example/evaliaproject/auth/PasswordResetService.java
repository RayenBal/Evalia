package com.example.evaliaproject.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.evaliaproject.entity.*;
import com.example.evaliaproject.repository.*;
import com.example.evaliaproject.service.ServiceEmail;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceEmail email;

    // URL de ta SPA Angular (configurable)
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void requestReset(String emailAddress) {
        var user = userRepository.findByEmail(emailAddress)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Révoquer anciens tokens RESET
        var all = tokenRepository.findAllByUser(user);
        all.stream()
                .filter(t -> t.getType() == TokenType.RESET_PASSWORD && !t.isRevoked())
                .forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(all);

        var code = generateCode(6);
        var token = Token.builder()
                .token(code)
                .type(TokenType.RESET_PASSWORD)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(2))
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);

        // ➜ Lien vers la page Angular (ou tu peux n’envoyer que le code)
        var link = frontendUrl + "/reset?code=" + code;

        email.sendEmail(
                user.getEmail(),
                "Réinitialisation du mot de passe",
                "Utilisez ce code pour réinitialiser votre mot de passe : " + code +
                        "\nOu cliquez sur : " + link +
                        "\nLe code expire dans 2 heures."
        );
    }

    public void confirmReset(String code, String newPassword) {
        var token = tokenRepository.findByTokenAndType(code, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new IllegalArgumentException("Code invalide"));

        if (token.isRevoked())
            throw new IllegalStateException("Token déjà utilisé");
        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Token expiré");

        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsedAt(LocalDateTime.now());
        token.setValidatedAt(LocalDateTime.now());
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    private String generateCode(int length){
        var chars = "0123456789";
        var r = new SecureRandom();
        var sb = new StringBuilder(length);
        for(int i=0;i<length;i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
