package com.example.LaundryApplication.configuration.service;

import com.example.LaundryApplication.configuration.dao.PasswordResetTokenRepository;
import com.example.LaundryApplication.configuration.dto.request.ChangePasswordRequest;
import com.example.LaundryApplication.configuration.dto.request.ForgotPasswordRequest;
import com.example.LaundryApplication.configuration.dto.request.RegisterRequest;
import com.example.LaundryApplication.configuration.dto.request.ResetPasswordRequest;
import com.example.LaundryApplication.configuration.model.PasswordResetToken;
import com.example.LaundryApplication.dao.UserRepository;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.InvalidRequestException;
import com.example.LaundryApplication.enums.Role;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.service.PricingService;
import com.example.LaundryApplication.utility.Email;
import com.example.LaundryApplication.utility.Validation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PricingService pricingService;
    private final Validation validation;
    private final Email email;


    @Transactional
    public void register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //Public registration = Seller
        user.setRole(Role.SELLER);

        User savedUser = userRepository.save(user);

        pricingService.createDefaultPricing(savedUser);

    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        User user = validation.getCurrentUser();

        // 1. Verify that the provided current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidRequestException("Wrong current password");
        }

        // 2. Verify that the new password and confirmation password match
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new InvalidRequestException("New passwords do not match");
        }

        // 3. Encode and save the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }

    @Transactional
    public void forgotPassword(@Valid ForgotPasswordRequest request) {

        User user = validation.findUserByEmail(request.getEmail());

        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    PasswordResetToken newToken = new PasswordResetToken();
                    newToken.setUser(user);
                    return newToken;
                });

        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );

        passwordResetTokenRepository.save(passwordResetToken);

        //update this with frontend-url
        String resetLink = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;

        CompletableFuture.runAsync(()->{
            email.sendPasswordResetEmail(user.getEmail(),resetLink);
        }).exceptionally(ex -> {
            log.error("Failed to send Reset Email link for user with email:- {}", user.getEmail());
            return null;
        });

    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(request.getToken())
                        .orElseThrow(() ->
                                new BusinessException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Reset token has expired");
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
