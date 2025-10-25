package com.example.evaliaproject.auth;

import com.example.evaliaproject.config.JwtService;
import com.example.evaliaproject.entity.TokenType;
import com.example.evaliaproject.entity.TypeUser;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.TokenRepository;
import com.example.evaliaproject.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins ="http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor

public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtService jwtService;

    @Data
    static class FirstLoginOtpRequest {
        @jakarta.validation.constraints.Email @NotBlank
        private String email;
        @NotBlank
        private String code;
    }

    @GetMapping("/getPanelist")
    public List<User> getUserbyTypeUser(){
        return userRepository.findByTypeUser(TypeUser.Paneliste);
    }
    @GetMapping("/getPanelist/{id}")
    public Optional<User> getUserbyId(@PathVariable Long id){
        return userRepository.findById(id);
    }
    @PostMapping("/authenticate/first-login/verify")
    public ResponseEntity<AuthenticationResponse> verifyFirstLogin(@RequestBody @Valid FirstLoginOtpRequest body) {
        var user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        var token = tokenRepository.findByTokenAndType(body.getCode(), TokenType.LOGIN_OTP)
                .orElseThrow(() -> new IllegalArgumentException("Code invalide"));

        if (!token.getUser().getId_user().equals(user.getId_user()))
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder()
                    .message("Code non associé à cet utilisateur").pending(true).build());

        if (token.isRevoked()) throw new IllegalStateException("Code déjà utilisé");
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalStateException("Code expiré");

        token.setRevoked(true);
        token.setValidatedAt(LocalDateTime.now());
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        // Marquer la 1ʳᵉ connexion terminée
        user.setFirstLoginCompleted(true);
        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(
                AuthenticationResponse.builder()
                        .token(jwt)
                        .message("Connexion réussie")
                        .pending(false)
                        .build()
        );}

//    @PostMapping("/register")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void register(@RequestBody @Valid RegisterRequest request) {
//        authenticationService.register(request);
//    }

    @GetMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> verifyEmail(@RequestParam("code") String code) {
        authenticationService.verifyEmail(code);
        return ResponseEntity.ok("E-mail vérifié. En attente validation admin.");

    }


//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
//        AuthenticationResponse response = authenticationService.register(request);
//
//               // On renvoie 202 Accepted ou 201 Created, au choix
//                      return ResponseEntity
//                              .status(HttpStatus.ACCEPTED)
//                              .body(response);
////        return ResponseEntity.ok(authenticationService.register(request));
//
//    }
@PostMapping(value = "/register", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<AuthenticationResponse> register(
        @Valid @ModelAttribute RegisterRequest request,
        @RequestPart(value = "registreCommerce", required = false) org.springframework.web.multipart.MultipartFile registreCommerce
) {
    AuthenticationResponse response = authenticationService.register(request, registreCommerce);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
}

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate (@RequestBody @Valid AuthenticationRequest request) {

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

//    @PostMapping("/register/announceur")
//    public ResponseEntity<AuthenticationResponse> registerAnnounceur(@Valid @RequestBody RegisterAnnounceurRequest r){
//        return ResponseEntity.status(HttpStatus.ACCEPTED)
//                .body(authenticationService.registerAnnounceur(r));
//    }
//
//    @PostMapping("/register/paneliste")
//    public ResponseEntity<AuthenticationResponse> registerPaneliste(@Valid @RequestBody RegisterPanelisteRequest r){
//        return ResponseEntity.status(HttpStatus.ACCEPTED)
//                .body(authenticationService.registerPaneliste(r));
//    }

}
