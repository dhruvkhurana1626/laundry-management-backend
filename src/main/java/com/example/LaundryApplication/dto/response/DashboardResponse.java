package com.example.LaundryApplication.dto.response;

import com.example.LaundryApplication.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class DashboardResponse {

    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Map<OrderStatus,Long> ordersPerStatus;

}
