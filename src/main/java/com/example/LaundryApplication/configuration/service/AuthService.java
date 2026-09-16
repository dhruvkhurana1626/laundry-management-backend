package com.example.LaundryApplication.configuration.service;

import com.example.LaundryApplication.dao.UserRepository;
import com.example.LaundryApplication.configuration.dto.request.RegisterRequest;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.enums.Role;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.service.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PricingService pricingService;


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
}
