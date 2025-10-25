package com.example.evaliabackoffice.dto;

import com.example.evaliabackoffice.entity.Motif;
import com.example.evaliabackoffice.entity.TypeUser;


import com.example.evaliabackoffice.entity.Motif;
import com.example.evaliabackoffice.entity.Reclamation;
import com.example.evaliabackoffice.entity.TypeUser;

public class ReclamationAdminDTO {
    private String id;
    private String content;
    private Motif motif;
    private TypeUser userType;
    private String userEmail;
    private String userFullName;
 private java.time.LocalDate createdAt;

    public ReclamationAdminDTO(Reclamation r) {
        this.id = r.getIdreclamation();
        this.content = r.getContent();
        this.motif = r.getMotif();
        this.userType = r.getUserType();
        if (r.getUser() != null) {
            this.userEmail = r.getUser().getEmail();
            this.userFullName = r.getUser().fullName();
        }
        this.createdAt = r.getCreatedAt();
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public Motif getMotif() { return motif; }
    public TypeUser getUserType() { return userType; }
    public String getUserEmail() { return userEmail; }
    public String getUserFullName() { return userFullName; }
public java.time.LocalDate getCreatedAt() { return createdAt; }
}