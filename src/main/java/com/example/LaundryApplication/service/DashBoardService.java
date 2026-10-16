package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.DashboardSummary;
import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dto.response.DashboardResponse;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.utility.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final OrderEntityDao orderEntityDao;
    private final Validation validation;

    public DashboardResponse getDashboard() {

        User user = validation.getCurrentUser();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth =
                today.withDayOfMonth(1).atStartOfDay();

        LocalDateTime startOfNextMonth =
                today.withDayOfMonth(1)
                        .plusMonths(1)
                        .atStartOfDay();

        DashboardSummary summary =
                orderEntityDao.getDashboardSummary(
                        user.getId(),
                        startOfMonth,
                        startOfNextMonth,
                        OrderStatus.DELIVERED,
                        OrderStatus.RECEIVED
                );

        DashboardResponse response = new DashboardResponse();

        response.setTotalOrders(summary.getTotalOrders());
        response.setTotalRevenue(summary.getTotalRevenue());

        Map<OrderStatus, Long> statusCount = new HashMap<>();
        statusCount.put(
                OrderStatus.DELIVERED,
                summary.getDeliveredCount()
        );
        statusCount.put(
                OrderStatus.RECEIVED,
                summary.getReceivedCount()
        );

        response.setOrdersPerStatus(statusCount);

        return response;
    }

}
