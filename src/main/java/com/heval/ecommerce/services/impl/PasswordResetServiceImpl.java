package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.entity.PasswordResetToken;
import com.heval.ecommerce.repository.PasswordResetTokenRepository;
import com.heval.ecommerce.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository repository;

    public String createToken(String email) {
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);

        repository.save(resetToken);
        return token;
    }

    public boolean isValid(String token) {
        Optional<PasswordResetToken> optional = repository.findByToken(token);
        if (optional.isEmpty()) return false;

        PasswordResetToken resetToken = optional.get();
        if (resetToken.isUsed()) return false;
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        return true;
    }

    public String getEmailByToken(String token) {
        return repository.findByToken(token).map(PasswordResetToken::getEmail).orElse(null);
    }

    public void markAsUsed(String token) {
        repository.findByToken(token).ifPresent(t -> {
            t.setUsed(true);
            repository.save(t);
        });
    }
}
