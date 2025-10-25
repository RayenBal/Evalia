package com.example.evaliabackoffice.service;
import com.example.evaliabackoffice.entity.User;
import com.example.evaliabackoffice.entity.UserStatus;
import com.example.evaliabackoffice.repository.TokenRepository;
import com.example.evaliabackoffice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
@Slf4j

@RequiredArgsConstructor
public class AdminValidationService{
        private final UserRepository userRepository;
        private final JdbcTemplate jdbcTemplate; // spring-boot-starter-jdbc requis
        private final TokenRepository tokenRepository;

    private final AuthenticationService authService;
    private final ServiceEmail email;
    @Transactional
    public void approveUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!u.isVerified()) {
            // on empêche l’approbation tant que l’email n’est pas vérifié
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'utilisateur n'a pas encore vérifié son e-mail.");
        }

        u.setNeedsAdminValidation(false);
        u.setEnabled(true);
        u.setStatus(UserStatus.APPROVED);
        userRepository.save(u);

        // 👉 e-mail “compte validé”
        try {
            email.sendAccountApproved(u);
        } catch (Exception e) {
            log.warn("Envoi e-mail 'validé' échoué pour {}: {}", u.getEmail(), e.getMessage());
        }
    }
//
//    @Transactional
//    public void rejectUser(Long id) {
//        User u = userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        u.setEnabled(false);
//        u.setNeedsAdminValidation(false);
//        u.setStatus(UserStatus.REJECTED);
//        userRepository.save(u);
//
//        tokenRepository.revokeAllByUserId(id);
//
//        try {
//            email.sendAccountRejected(u); // (celui-ci fonctionnait déjà chez toi)
//        } catch (Exception e) {
//            log.warn("Envoi e-mail 'rejeté' échoué pour {}: {}", u.getEmail(), e.getMessage());
//        }
//    }

//    @Transactional
//    public void approveUser(Long id) {
//        User u = userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (!u.isVerified()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'utilisateur n'a pas encore vérifié son e-mail.");
//        }
//
//        u.setNeedsAdminValidation(false);
//        u.setEnabled(true);
//        u.setStatus(UserStatus.APPROVED);
//        userRepository.save(u);
//
//        // e-mail d’info (on ne rollback pas si l’envoi échoue)
//        try { email.sendAccountApproved(u); }
//        catch (Exception e) { log.warn("Envoi e-mail 'validé' échoué pour {}: {}", u.getEmail(), e.getMessage()); }
//    }

//    @Transactional
//    public void rejectUser(Long id) {
//        User u = userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        u.setEnabled(false);
//        u.setNeedsAdminValidation(false);
//        u.setStatus(UserStatus.REJECTED);
//        userRepository.save(u);
//
//        // invalider tous ses tokens (si tu utilises des tokens)
//        tokenRepository.revokeAllByUserId(id);
//
//        // e-mail d’info
//        try { email.sendAccountRejected(u); }
//        catch (Exception e) { log.warn("Envoi e-mail 'rejeté' échoué pour {}: {}", u.getEmail(), e.getMessage()); }
//    }
@Transactional
public void rejectUser(Long id) {
    User u = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    u.setEnabled(false);
    u.setNeedsAdminValidation(false);
    u.setStatus(UserStatus.REJECTED);
    userRepository.save(u);

    // invalider tous ses tokens (si tu utilises des tokens)
    tokenRepository.revokeAllByUserId(id);

    // e-mail d’info
    try { email.sendAccountRejected(u); }
    catch (Exception e) { log.warn("Envoi e-mail 'rejeté' échoué pour {}: {}", u.getEmail(), e.getMessage()); }
}
}
