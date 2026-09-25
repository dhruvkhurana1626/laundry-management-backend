package com.example.LaundryApplication.configuration.dao;


import com.example.LaundryApplication.configuration.model.RefreshToken;
import com.example.LaundryApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);

}