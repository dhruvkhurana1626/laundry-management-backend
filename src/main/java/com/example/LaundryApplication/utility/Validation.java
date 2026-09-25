package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.configuration.dao.RefreshTokenRepository;
import com.example.LaundryApplication.configuration.model.RefreshToken;
import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dao.UserRepository;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.ResourceNotFoundException;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Validation {

    private final OrderEntityDao orderEntityDao;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public OrderEntity findOrderById_ReturnOrder(Integer id) {
        return orderEntityDao.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));
    }

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("No user found with this Email")
                        );
    }

    //Look for token in the Repo
    //throw expection if not found
    public RefreshToken findByToken(@NotBlank(message = "Refresh token is required") String refreshToken) {
        return  refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(()-> new BusinessException("Invalid Refresh Token") );

    }

    //to validate if the token is expired to not
    public void isTokenNotExpired(RefreshToken refreshToken) {
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException("Refresh Token has Expired");
        }
    }

}
