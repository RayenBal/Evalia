package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Token;
import com.example.evaliabackoffice.entity.TokenType;
import com.example.evaliabackoffice.entity.User;
import com.example.evaliabackoffice.repository.RoleRepository;
import com.example.evaliabackoffice.repository.TokenRepository;
import com.example.evaliabackoffice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final ServiceEmail serviceEmail;
//    private Token token;
    private final PasswordEncoder passwordEncoder;



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

    @Transactional
    public void resendVerificationFor(User user) {
        String code = generateActivationTokenForUser(user); // ⚠️ ta méthode existe déjà dans cette classe
        String verificationLink = "http://localhost:8081/api/v1/auth/verify?code=" + code;
        serviceEmail.sendVerificationEmail(user.getEmail(), verificationLink);
    }}