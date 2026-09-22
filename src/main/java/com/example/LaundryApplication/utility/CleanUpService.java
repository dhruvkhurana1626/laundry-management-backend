package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.configuration.dao.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CleanUpService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredTokens() {

        passwordResetTokenRepository
                .deleteByExpiryDateBefore(LocalDateTime.now());
    }

}
