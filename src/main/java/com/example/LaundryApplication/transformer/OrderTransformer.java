package com.example.LaundryApplication.transformer;

import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.model.OrderEntity;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

public class OrderTransformer {
    public static OrderEntity orderRequestToOrder(@Valid OrderRequest orderRequest) {
        OrderEntity orderEntity = OrderEntity.builder()
                .customerName(orderRequest.getCustomerName())
                .phone(orderRequest.getPhone())
                .email(orderRequest.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        return orderEntity;
    }

    public static OrderResponse orderToOrderResponse(OrderEntity savedOrder) {
        OrderResponse orderResponse = OrderResponse.builder()
                .id(savedOrder.getId())
                .customerName(savedOrder.getCustomerName())
                .phone(savedOrder.getPhone())
                .email(savedOrder.getEmail())
                .createdAt(savedOrder.getCreatedAt())
                .orderStatus(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .garments(GarmentTransformer.garmentListToGarmentResponseList(savedOrder.getGarmentList()))
                .build();

        return orderResponse;
    }
}
