package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.ResourceNotFoundException;
import com.example.LaundryApplication.model.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Validation {

    private final OrderEntityDao orderEntityDao;

    public OrderEntity findOrderById_ReturnOrder(Integer id) {
        return orderEntityDao.findById(Long.valueOf(id))
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));
    }

}
