package com.example.evaliabackoffice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserAdmin")
@Table(name="_user")
@EntityListeners(AuditingEntityListener.class)

@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {
//    @Id
//    @GeneratedValue (strategy = GenerationType.AUTO)
//    @Column(name = "id_user")
//    private Long id_user;
//    private String firstname;
//    private String lastname;
//    private String email;
//    private String password;
//    private Integer age;
//    private String jobTitle;
//    private boolean enabled;
//    private boolean verified;
//    private String activationCode;
//    private String numTéléphone;
//    private boolean needsAdminValidation;
//    @CreatedDate
//    @Column(nullable = false,updatable = false)
//    private LocalDateTime createdDate;
//    @LastModifiedDate
//    @Column(insertable = false)
//    private LocalDateTime LastModifiedDate;
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "users_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private List<Role> roles = new ArrayList<>();
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.getNameRole()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//    @Override
//    public boolean isAccountNonLocked() {
//
//        return true;
//    }
//
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return this.enabled;
//    }
//    public String getStatus() {
//        if (enabled) return "Validé";
//        if (needsAdminValidation) return "En attente";
//        return "Rejeté";
//    }
//
//
//    public String fullName(){return firstname + " " + lastname;}
//
//}
@Id
@GeneratedValue (strategy = GenerationType.AUTO)

//@UuidGenerator
private Long id_user;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private boolean firstLoginCompleted;
    @Column(nullable = false)
    @NotEmpty(message = "Age range is mandatory")
    @jakarta.validation.constraints.Pattern(
            regexp = "18_25|26_35|36_45|46_60|60_plus",
            message = "Invalid age range"
    )

    private String ageRange;
    @Column(length = 34)
    private String iban;
    @Column(length = 255)
    private String deliveryAddress;
    private String jobTitle;
    private String companyName;
    private boolean enabled;
    private boolean verified;
    private String activationCode;
    private String numTelephone;
    private boolean needsAdminValidation;
    private String registreCommercePath;
    private String registreCommerceOriginalName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime LastModifiedDate;
    @Enumerated(EnumType.STRING)
    TypeUser typeUser;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getNameRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {

        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {

        return this.enabled;
    }
    public String fullName(){return firstname + " " + lastname;}
    public String getAgeRange() { return ageRange; }
    public void setIban(String iban) {
        this.iban = iban == null ? null : iban.replaceAll("\\s+","").toUpperCase();
    }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }
}
