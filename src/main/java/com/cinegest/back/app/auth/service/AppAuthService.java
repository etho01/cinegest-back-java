package com.cinegest.back.app.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinegest.back.app.auth.dto.AppLoginRequestDto;
import com.cinegest.back.app.auth.dto.AppLoginReturnDto;
import com.cinegest.back.app.auth.dto.AppRegisterRequestDto;
import com.cinegest.back.app.auth.dto.ForgotPasswordRequestDto;
import com.cinegest.back.app.auth.dto.ResetPasswordRequestDto;
import com.cinegest.back.global.CustomAuthenticationToken;
import com.cinegest.back.global.entity.PasswordResetToken;
import com.cinegest.back.global.entity.User;
import com.cinegest.back.global.repository.PasswordResetTokenRepository;
import com.cinegest.back.global.repository.UserRepository;
import com.cinegest.back.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class AppAuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AppLoginReturnDto login(AppLoginRequestDto request) {
        authenticationManager.authenticate(
                new CustomAuthenticationToken(request.getEmail(), request.getPassword(), "app", 0)
        );

        User user = userRepository.findByEmailAndTypeAndOriginID(request.getEmail(), "app", 0)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtUtil.generateToken(user);

        return AppLoginReturnDto.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AppLoginReturnDto register(AppRegisterRequestDto request) {
        if (userRepository.findByEmailAndTypeAndOriginID(request.getEmail(), "app", 0).isPresent()) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .type("app")
                .originID(0)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return AppLoginReturnDto.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequestDto request) {
        userRepository.findByEmailAndTypeAndOriginID(request.getEmail(), "app", 0)
                .ifPresent(user -> {
                    passwordResetTokenRepository.deleteAllByUserEmail(user.getEmail());

                    String tokenValue = UUID.randomUUID().toString();
                    PasswordResetToken resetToken = PasswordResetToken.builder()
                            .token(tokenValue)
                            .user(user)
                            .expiresAt(LocalDateTime.now().plusHours(1))
                            .build();
                    passwordResetTokenRepository.save(resetToken);

                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(user.getEmail());
                    message.setSubject("Réinitialisation de votre mot de passe");
                    message.setText("Bonjour " + user.getFirstName() + ",\n\n"
                            + "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n"
                            + frontendUrl + "/reset-password?token=" + tokenValue + "\n\n"
                            + "Ce lien est valable 1 heure.\n\n"
                            + "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.");
                    mailSender.send(message);
                });
    }

    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Le token a expiré");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Le token a déjà été utilisé");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}

