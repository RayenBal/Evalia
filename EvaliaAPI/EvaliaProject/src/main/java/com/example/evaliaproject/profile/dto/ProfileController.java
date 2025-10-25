package com.example.evaliaproject.profile.dto;
import com.example.evaliaproject.entity.TypeUser;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.profile.dto.ChangePasswordRequest;
import com.example.evaliaproject.profile.dto.MeDto;
import com.example.evaliaproject.profile.dto.UpdateMeRequest;
import com.example.evaliaproject.repository.TokenRepository;
import com.example.evaliaproject.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/profile")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    private static final Set<String> AGE_RANGES =
            Set.of("18_25", "26_35", "36_45", "46_60", "60_plus");
    private static final String IBAN_REGEX = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$";
    // -------- Me (lecture)
    @GetMapping("/me")
    public MeDto me(@AuthenticationPrincipal User current) {
        if (current == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return toDto(current);
    }

    // -------- Me (mise à jour)
    @PutMapping("/me")
    public MeDto update(@AuthenticationPrincipal User current,
                        @Valid @RequestBody UpdateMeRequest body) {
        if (current == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (body.getFirstname() != null) current.setFirstname(body.getFirstname().trim());
        if (body.getLastname()  != null) current.setLastname(body.getLastname().trim());
        if (body.getNumTelephone() != null) current.setNumTelephone(body.getNumTelephone().trim());

        if (current.getTypeUser() == TypeUser.Announceur) {
            if (body.getCompanyName() != null) current.setCompanyName(emptyToNull(body.getCompanyName()));
            // jobTitle/ageRange ignorés pour annonceur
        } else { // Paneliste
            if (body.getJobTitle() != null) current.setJobTitle(emptyToNull(body.getJobTitle()));
            if (body.getAgeRange() != null) {
                String ar = body.getAgeRange().trim();
                if (!AGE_RANGES.contains(ar)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tranche d'âge invalide");
                }
                current.setAgeRange(ar);
            }
            // IBAN: vide -> null, sinon regex + checksum
//            if (body.getIban() != null) {
//                String iban = emptyToNull(body.getIban());
//                if (iban == null) {
//                    current.setIban(null);
//                } else {
//                    iban = normalizeIban(iban);
//                    if (!iban.matches(IBAN_REGEX) || !ibanChecksumOk(iban)) {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IBAN invalide");
//                    }
//                    current.setIban(iban);
//                }
//            }
            if (body.getIban() != null) {
                String iban = emptyToNull(body.getIban());
                if (iban == null) {
                    current.setIban(null); // autorise effacement
                } else {
                    iban = normalizeIban(iban);                         // <— ta ligne
                    if (!iban.matches(IBAN_REGEX) || !ibanChecksumOk(iban)) { // <— ta condition
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IBAN invalide");
                    }
                    current.setIban(iban);
                }
            }

            if (body.getDeliveryAddress() != null) {
                current.setDeliveryAddress(emptyToNull(body.getDeliveryAddress())); // ✅
            }
        }

        userRepository.save(current);
        return toDto(current);
    }


    private static String emptyToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private static String normalizeIban(String s) {
        return s == null ? null : s.replaceAll("\\s+", "").toUpperCase();
    }

    // IBAN checksum Mod-97 (ISO 13616)
    private static boolean ibanChecksumOk(String iban) {
        if (iban == null || iban.length() < 4) return false;
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numeric = new StringBuilder(rearranged.length());
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) numeric.append(c);
            else if (c >= 'A' && c <= 'Z') numeric.append((c - 'A') + 10);
            else return false;
        }
        // réduction mod 97 sans overflow
        int mod = 0;
        for (int i = 0; i < numeric.length(); i++) {
            mod = (mod * 10 + (numeric.charAt(i) - '0')) % 97;
        }
        return mod == 1;
    }

    // -------- Changement de mot de passe
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal User current,
                               @Valid @RequestBody ChangePasswordRequest req) {
        if (current == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (!passwordEncoder.matches(req.getCurrentPassword(), current.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe actuel incorrect");
        }
        if (passwordEncoder.matches(req.getNewPassword(), current.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nouveau mot de passe doit être différent");
        }

        current.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(current);

        // (optionnel mais recommandé) révoquer tokens/OTP existants
        try { tokenRepository.revokeAllByUserId(current.getId_user()); } catch (Exception ignored) {}
    }

    //private static String emptyToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private static MeDto toDto(User u) {
        return MeDto.builder()
                .id_user(u.getId_user())
                .firstname(u.getFirstname())
                .lastname(u.getLastname())
                .email(u.getEmail())
                .numTelephone(u.getNumTelephone())
                .typeUser(u.getTypeUser())
                .companyName(u.getCompanyName())
                .jobTitle(u.getJobTitle())
                .ageRange(u.getAgeRange())
                .iban(u.getIban())
                .deliveryAddress(u.getDeliveryAddress())
                .verified(u.isVerified())
                .enabled(u.isEnabled())
                .needsAdminValidation(u.isNeedsAdminValidation())
                .firstLoginCompleted(u.isFirstLoginCompleted())
                .build();
    }
}
