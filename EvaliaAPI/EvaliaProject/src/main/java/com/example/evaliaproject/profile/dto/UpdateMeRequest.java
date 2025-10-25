package com.example.evaliaproject.profile.dto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UpdateMeRequest {
    @Size(min = 1, max = 100)
    private String firstname;
    @Size(min = 1, max = 100)
    private String lastname;
    @Size(min = 0, max = 30)
    private String numTelephone;

    // Spécifiques selon type
    private String companyName; // Announceur
    private String jobTitle;    // Paneliste
    private String ageRange;
    @Size(min = 0, max = 64)
    private String iban;
    @Size(min = 0, max = 255)
    private String deliveryAddress;// Paneliste: 18_25 | 26_35 | 36_45 | 46_60 | 60_plus
}

