package com.example.LaundryApplication.dao;

import com.example.LaundryApplication.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEntityDao extends JpaRepository<OrderEntity, String> {
}
