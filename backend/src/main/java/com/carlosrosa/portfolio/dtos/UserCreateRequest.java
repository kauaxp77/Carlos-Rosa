package com.carlosrosa.portfolio.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    private String role; // e.g. "ADMIN", "EDITOR"
}
