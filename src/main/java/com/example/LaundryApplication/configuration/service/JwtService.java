package com.example.LaundryApplication.configuration.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // Generate AccessToken
    public String generateAccessToken(String username) {

        return Jwts.builder()
                .subject(username)
                .claim("type", "ACCESS")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + accessExpiration)
                )
                .signWith(secretKey)
                .compact();
    }

    //Generate RefreshToken
    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .subject(username)
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + refreshExpiration)
                )
                .signWith(secretKey)
                .compact();
    }

    // Extract username/email from JWT
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    // Validate JWT
    public boolean isTokenValid(String token, String username) {

        try {
            String extractedUsername = extractUsername(token);

            return extractedUsername.equals(username)
                    && !isTokenExpired(token);

        } catch (Exception e) {
            return false;
        }
    }

    //Check if Token is Access Token or Not
    public boolean isAccessToken(String token) {

        try {
            String type = extractAllClaims(token).get("type", String.class);
            return "ACCESS".equals(type);
        } catch (Exception e) {
            return false;
        }

    }

    //Check if Token is Refresh Token on Not
    public boolean isRefreshToken(String token) {

        try {
            String type = extractAllClaims(token).get("type", String.class);
            return "REFRESH".equals(type);
        } catch (Exception e) {
            return false;
        }

    }

    // Check expiration
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Parse and verify JWT
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}