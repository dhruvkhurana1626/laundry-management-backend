package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.OrderEntityDao;
import com.example.LaundryApplication.dto.response.DashboardResponse;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.OrderEntity;
import com.example.LaundryApplication.utility.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final OrderEntityDao orderEntityDao;
    private final Validation validation;

    public DashboardResponse getDashboard() {

        LocalDateTime startOfMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .atStartOfDay();

        List<OrderEntity> orderEntityList =
                orderEntityDao.findByUserId(validation.getCurrentUser().getId())
                        .stream()
                        .filter(order -> !order.getCreatedAt().isBefore(startOfMonth))
                        .toList();

        DashboardResponse dashboardResponse = new DashboardResponse();

        //Total Orders
        dashboardResponse.setTotalOrders((long) orderEntityList.size());

        //Total Revenue
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for(OrderEntity orderEntity : orderEntityList){
            totalRevenue = totalRevenue.add(orderEntity.getTotalAmount());
        }

        dashboardResponse.setTotalRevenue(totalRevenue);

        //Orders per Status
        Map<OrderStatus,Long> statusCount = new HashMap<>();

        long RECEIVED  = 0;
        long DELIVERED = 0;

        for(OrderEntity orderEntity : orderEntityList) {
            if(orderEntity.getStatus()==OrderStatus.DELIVERED) DELIVERED++;
            if(orderEntity.getStatus()==OrderStatus.RECEIVED) RECEIVED++;
        }

        statusCount.put(OrderStatus.DELIVERED,DELIVERED);
        statusCount.put(OrderStatus.RECEIVED,RECEIVED);

        dashboardResponse.setOrdersPerStatus(statusCount);

        return dashboardResponse;
    }

}
