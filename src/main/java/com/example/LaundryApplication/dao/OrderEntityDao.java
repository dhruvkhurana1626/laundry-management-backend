package com.example.LaundryApplication.dao;

import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Query("""
    SELECT
        COUNT(o) AS totalOrders,
        COALESCE(SUM(o.totalAmount), 0) AS totalRevenue,
        SUM(CASE WHEN o.status = :delivered
            THEN 1L ELSE 0L END) AS deliveredCount,
        SUM(CASE WHEN o.status = :received
            THEN 1L ELSE 0L END) AS receivedCount
    FROM OrderEntity o
    WHERE o.user.id = :userId
      AND o.createdAt >= :start
      AND o.createdAt < :end
    """)
    DashboardSummary getDashboardSummary(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("delivered") OrderStatus delivered,
            @Param("received") OrderStatus received
    );

    List<OrderEntity> findByUserId(Long userId);

    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

}