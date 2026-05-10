package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.ecxeption.ResourceNotFoundException;
import com.example.LaundryApplication.model.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Validation {

    private final OrderEntityDao orderEntityDao;

    public OrderEntity findOrderById_ReturnOrder(String id) {
        return orderEntityDao.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));
    }
}
