package com.example.LaundryApplication.dao;

import com.example.LaundryApplication.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderEntityDao extends JpaRepository<OrderEntity, Integer> {

    @Query("""
            SELECT DISTINCT o
            FROM OrderEntity o
            LEFT JOIN FETCH o.garmentList
            WHERE o.id = :id
            """)
    Optional<OrderEntity> findByIdWithGarments(@Param("id") Integer id);

}