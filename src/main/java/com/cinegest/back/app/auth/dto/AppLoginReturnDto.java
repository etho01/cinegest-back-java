package com.cinegest.back.app.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppLoginReturnDto {
    private String token;
    private String firstName;
    private String lastName;
    private String email;
}
