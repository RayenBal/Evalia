package com.example.evaliabackoffice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "AdminBackofficeEntity")
@Table(name="_admin")

public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idadmin;
    @NotEmpty(message = "Le prénom est requis")
    private String firstname;
    @NotEmpty(message = "Le nom est requis")
    private String lastname;
    @NotEmpty(message = "L'email est requis")
    @Email(message = "Email invalide")
    private String email;
    @Column(nullable = false)
    @NotEmpty(message = "Le mot de passe est requis")
    @Size(min=8, message = "minimum Password length is 8 characters")
    private String password;
    @Transient
    @NotEmpty
    private String confirmPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admins_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private List<Role> roles = new ArrayList<>();

//    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL)
//    private List<Announcement> announcements = new ArrayList<>();


}

