package com.cinegest.back.app.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cinegest.back.app.auth.dto.AppLoginRequestDto;
import com.cinegest.back.app.auth.dto.AppLoginReturnDto;
import com.cinegest.back.app.auth.dto.AppRegisterRequestDto;
import com.cinegest.back.app.auth.dto.ForgotPasswordRequestDto;
import com.cinegest.back.app.auth.dto.ResetPasswordRequestDto;
import com.cinegest.back.app.auth.service.AppAuthService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;

    @PostMapping("/login")
    public ResponseEntity<AppLoginReturnDto> login(@Valid @RequestBody AppLoginRequestDto request) {
        return ResponseEntity.ok(appAuthService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AppLoginReturnDto> register(@Valid @RequestBody AppRegisterRequestDto request) {
        return ResponseEntity.ok(appAuthService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        appAuthService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        appAuthService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}


