package com.example.LaundryApplication.configuration.controller;

import com.example.LaundryApplication.configuration.dto.request.*;
import com.example.LaundryApplication.configuration.dto.response.LoginResponse;
import com.example.LaundryApplication.configuration.service.AuthService;
import com.example.LaundryApplication.configuration.service.RateLimitService;
import com.example.LaundryApplication.ecxeption.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimitService rateLimitService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpServletRequest) {

        String ip = httpServletRequest.getRemoteAddr();
        String email = request.getEmail().toLowerCase().trim();
        String rateLimitKey = "login:" + email + ":" + ip;

        //Ratelimiting added
        if (!rateLimitService.isAllowed(rateLimitKey)) {
            throw new TooManyRequestsException("Too many login attempts. Please try again in 15 minutes.");
        }

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok("Seller registered successfully");
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {

        authService.changePassword(changePasswordRequest);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpServletRequest) {

        String ip = httpServletRequest.getRemoteAddr();
        String email = request.getEmail().toLowerCase().trim();
        String rateLimitKey = "login:" + email + ":" + ip;

        //Ratelimiting added
        if (!rateLimitService.isAllowed(rateLimitKey)) {
            throw new TooManyRequestsException("Too many login attempts. Please try again in 15 minutes.");
        }

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                "Password reset link has been sent."
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok("Password reset successfully");
    }


}