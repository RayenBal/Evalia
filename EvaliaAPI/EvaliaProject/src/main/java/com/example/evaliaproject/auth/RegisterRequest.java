package com.example.evaliaproject.auth;

import com.example.evaliaproject.entity.TypeUser;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank private String firstname;
    @NotBlank private String lastname;
    @Size(max = 255)
    private String deliveryAddress;
    @Email @NotBlank private String email;
    @Size(min = 8) @NotBlank private String password;

    @NotBlank private String numTelephone;

    @NotNull private TypeUser typeUser; // Announceur | Paneliste

    // Spécifiques potentiels
    private String companyName;  // requis si Announceur
    private String jobTitle;     // requis si Paneliste
//    private Integer age;         // requis si Paneliste

    @Pattern(regexp="(18_25|26_35|36_45|46_60|60_plus)?")
    private String ageRange;     // requis si Paneliste

    @Pattern(
            regexp = "([A-Za-z]{2}\\d{2}[A-Za-z0-9]{9,30})?",
            message = "IBAN invalide"
    )
    private String iban;
//    private String role; // optionnel (on déduit du typeUser en pratique)

    // Validation conditionnelle
    @AssertTrue(message = "companyName est requis pour un Annonceur")
    public boolean isCompanyNameValidForAnnounceur() {
        if (typeUser == TypeUser.Announceur) {
            return companyName != null && !companyName.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "jobTitle est requis pour un Paneliste")
    public boolean isJobTitleValidForPaneliste() {
        if (typeUser == TypeUser.Paneliste) {
            return jobTitle != null && !jobTitle.isBlank();
        }
        return true;
    }

//    @AssertTrue(message = "age est requis pour un Paneliste et doit être >= 18")
//    public boolean isAgeValidForPaneliste() {
//        if (typeUser == TypeUser.Paneliste) {
//            return age != null && age >= 18;
//        }
//        return true;
//    }

    @AssertTrue(message = "ageRange est requis pour un Paneliste")
    public boolean isAgeRangeValidForPaneliste() {
        if (typeUser == TypeUser.Paneliste) {
            return ageRange != null && !ageRange.isBlank();
        }
        return true;
    }
    
    @AssertTrue(message = "IBAN est requis pour un Paneliste")
    public boolean isIbanValidForPaneliste() {
        if (typeUser == TypeUser.Paneliste) {
            return iban != null && !iban.isBlank();
        }
        return true;
    }

    // ⬇ IBAN  si paneliste
    @AssertTrue(message = "IBAN est requis pour un Paneliste")
    public boolean isIbanRequiredForPaneliste() {
        return typeUser != TypeUser.Paneliste || (iban != null && !iban.isBlank());
    }
    @AssertTrue(message = "Adresse de livraison requise pour un Paneliste")
    public boolean isDeliveryAddressRequiredForPaneliste() {
        return typeUser != TypeUser.Paneliste || (deliveryAddress != null && !deliveryAddress.isBlank());
    }
}

















//    @NotEmpty(message= "Firstname is mandatory")
//    @NotBlank(message= "Firstname is mandatory")
//    private String firstname;
//    @NotEmpty(message= "Lastname is mandatory")
//    @NotBlank(message= "Lastname is mandatory")
//    private String lastname;
//    @Email(message = "Email is not formatted")
//    @NotEmpty(message= "Email is mandatory")
//    @NotBlank(message= "Email is mandatory")
//    private String email;
//    @NotEmpty(message= "Password is mandatory")
//    @NotBlank(message= "Password is mandatory")
//    @Size(min=8, message = "minimum Password length is 8 characters")
//    private String password;
//    private String role;
//    @NotEmpty(message = "Age range is mandatory")
//    @jakarta.validation.constraints.Pattern(
//            regexp = "18_25|26_35|36_45|46_60|60_plus",
//            message = "Invalid age range"
//    )
//    private String ageRange;
//    @Enumerated(EnumType.STRING)
//    TypeUser typeUser;
//    @CreatedDate
//    @Column(nullable = false,updatable = false)
//    private LocalDateTime createdDate;
//    private String jobTitle;
//    private String companyName;
//    private boolean enabled;
//    private boolean verified;
//    private String activationCode;
//    private String numTelephone;
//    private boolean needsAdminValidation;








