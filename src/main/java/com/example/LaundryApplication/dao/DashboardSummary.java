package com.example.LaundryApplication.dao;

import java.math.BigDecimal;

public interface DashboardSummary {
    Long getTotalOrders();
    BigDecimal getTotalRevenue();
    Long getDeliveredCount();
    Long getReceivedCount();
}