package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dao.UserRepository;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.ResourceNotFoundException;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Validation {

    private final OrderEntityDao orderEntityDao;
    private final UserRepository userRepository;

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

}
