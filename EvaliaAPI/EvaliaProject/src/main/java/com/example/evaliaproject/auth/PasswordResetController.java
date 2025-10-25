package com.example.evaliaproject.auth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth/password")
@CrossOrigin(origins ="http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
@Validated
public class PasswordResetController {
    private final PasswordResetService resetService;

    // POST /api/v1/auth/password/forgot
    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestBody ForgotRequest req) {
        resetService.requestReset(req.getEmail());
        return ResponseEntity.ok().build();
    }

    // POST /api/v1/auth/password/reset
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody ResetRequest req) {
        resetService.confirmReset(req.getCode(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class ForgotRequest {
        @Email @NotBlank
        private String email;
    }

    @Data
    public static class ResetRequest {
        @NotBlank
        private String code;
        @NotBlank
        private String newPassword; // côté Angular, impose minLength 8
    }
}
