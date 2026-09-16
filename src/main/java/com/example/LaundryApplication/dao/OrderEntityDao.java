package com.example.LaundryApplication.dao;

import com.example.LaundryApplication.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderEntityDao extends JpaRepository<OrderEntity, Integer>, JpaSpecificationExecutor<OrderEntity> {

    @Query("""
            SELECT DISTINCT o
            FROM OrderEntity o
            LEFT JOIN FETCH o.garmentList
            WHERE o.id = :id
            """)
    Optional<OrderEntity> findByIdWithGarments(@Param("id") Integer id);

    List<OrderEntity> findByUserId(Long userId);

    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

}