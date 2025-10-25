package com.example.evaliaproject.profile.dto;
import com.example.evaliaproject.entity.TypeUser;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class MeDto {
    private Long id_user;
    private String firstname;
    private String lastname;
    private String email;
    private String numTelephone;
    private TypeUser typeUser;
    private String companyName;   // annonceur
    private String jobTitle;      // paneliste
    private String ageRange;
    private String iban;
    private String deliveryAddress;// paneliste
    private boolean verified;
    private boolean enabled;
    private boolean needsAdminValidation;
    private boolean firstLoginCompleted;
}