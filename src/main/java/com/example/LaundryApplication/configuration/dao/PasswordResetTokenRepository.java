package com.example.LaundryApplication.configuration.dao;


import com.example.LaundryApplication.configuration.model.PasswordResetToken;
import com.example.LaundryApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    Optional<PasswordResetToken> findByUser(User user);

    @Modifying
    @Transactional
    void deleteByUserId(Long id);

    Optional<PasswordResetToken> findByUserId(Long id);
}
