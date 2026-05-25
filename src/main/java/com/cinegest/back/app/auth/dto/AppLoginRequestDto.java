package com.cinegest.back.app.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppLoginRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
