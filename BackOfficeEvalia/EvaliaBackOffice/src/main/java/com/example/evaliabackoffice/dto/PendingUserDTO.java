
package com.example.evaliabackoffice.dto;

import com.example.evaliabackoffice.entity.User;
import com.example.evaliabackoffice.entity.Role;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class PendingUserDTO {

    private  String status;
    private Long id_user;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String formattedDate;
    private boolean enabled;
    private boolean needsAdminValidation;

    public PendingUserDTO(User user) {
        this.id_user = user.getId_user();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.role = user.getRoles().stream()
                .map(Role::getNameRole)
                .collect(Collectors.joining(", "));
        this.enabled = user.isEnabled();
        this.needsAdminValidation = user.isNeedsAdminValidation();
        this.formattedDate = user.getCreatedDate() != null
                ? user.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";

        if (user.isEnabled()) {
            this.status = "Validé";
        } else if (!user.isEnabled() && !user.isNeedsAdminValidation()) {
            this.status = "Rejeté";
        } else {
            this.status = "En attente";
        }

    }


    public Long getId_user() { return id_user; }
    public String getStatus() {
        if (enabled) return "Validé";
        if (needsAdminValidation) return "En attente";
        return "Rejeté";
    }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public boolean isNeedsAdminValidation() { return needsAdminValidation; }

    public String getFormattedDate() { return formattedDate; }
}
