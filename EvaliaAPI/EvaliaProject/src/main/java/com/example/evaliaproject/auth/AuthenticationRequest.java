package com.example.evaliaproject.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @Email(message = "Email is not formatted")
    @NotEmpty(message= "Email is mandatory")
    @NotBlank(message= "Email is mandatory")
    private String email;
    @NotEmpty(message= "Password is mandatory")
    @NotBlank(message= "Password is mandatory")
    @Size(min=8, message = "minimum Password length is 8 characters")
    private String password;

}
