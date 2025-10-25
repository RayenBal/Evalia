package com.example.evaliaproject.profile.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ChangePasswordRequest {
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Size(min = 8, max = 128, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String newPassword;
}
