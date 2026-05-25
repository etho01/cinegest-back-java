package com.cinegest.back.service;

import com.cinegest.back.entity.PasswordResetToken;
import com.cinegest.back.entity.User;
import com.cinegest.back.repository.PasswordResetTokenRepository;
import com.cinegest.back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Transactional
    public void forgotPassword(String email) {
        // On retourne toujours un succès pour ne pas révéler si l'email existe
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.deleteAllByUserEmail(email);

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .build();

            tokenRepository.save(resetToken);

            try {
                mailService.sendPasswordResetEmail(email, token);
            } catch (Exception e) {
                log.warn("Impossible d'envoyer l'email de réinitialisation à {} : {}", email, e.getMessage());
                log.info("Token de réinitialisation (dev) : {}", token);
            }
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token invalide ou inexistant"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Ce lien a déjà été utilisé");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Ce lien a expiré, veuillez refaire une demande");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
