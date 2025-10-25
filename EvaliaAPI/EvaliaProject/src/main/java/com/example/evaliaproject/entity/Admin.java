package com.example.evaliaproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "AdminUserEntity")
//@EqualsAndHashCode(callSuper=true)
@Table(name="_admin")
public class Admin  {
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
    @NotEmpty(message = "Le mot de passe est requis")
    @Size(min=8, message = "minimum Password length is 8 characters")
    private String password;
    @NotEmpty
    private String confirmPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admins_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Announce> announcements = new ArrayList<>();


}

