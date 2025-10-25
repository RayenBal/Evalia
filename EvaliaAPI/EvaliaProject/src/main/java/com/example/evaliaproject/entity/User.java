package com.example.evaliaproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserFront")
@Table(name="_user")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)

@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {
    @Id
  @GeneratedValue (strategy = GenerationType.AUTO)

    //@UuidGenerator
    private Long id_user;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
  private boolean firstLoginCompleted;
  private String registreCommercePath;
  private String registreCommerceOriginalName;
//  @Column(nullable = false)
//  @NotEmpty(message = "Age range is mandatory")
  @jakarta.validation.constraints.Pattern(
          regexp = "18_25|26_35|36_45|46_60|60_plus",
          message = "Invalid age range"
  )
  private String ageRange;
    private String jobTitle;
  @Column(length = 34)
  private String iban;
  @Column(length = 255)
  private String deliveryAddress;

  private String companyName;
    private boolean enabled;
    private boolean verified;
    private String activationCode;
    private String numTelephone;
    private boolean needsAdminValidation;

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

  @OneToMany(mappedBy = "panelist",  cascade=CascadeType.ALL, orphanRemoval=true)
  @JsonManagedReference
  @ToString.Exclude
  private List<Planning> plannings  = new ArrayList<>();

  @OneToMany(mappedBy = "owner",  cascade=CascadeType.ALL, orphanRemoval=true)
  @JsonManagedReference
  @ToString.Exclude
  private List<Planning> plannings1  = new ArrayList<>();
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
  public void setAgeRange(String ageRange) { this.ageRange = ageRange; }
  public void setIban(String iban) {
    this.iban = iban == null ? null : iban.replaceAll("\\s+","").toUpperCase();
  }
}
