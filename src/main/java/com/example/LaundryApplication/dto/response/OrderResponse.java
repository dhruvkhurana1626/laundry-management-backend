package com.example.LaundryApplication.dto.response;

import com.example.LaundryApplication.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class OrderResponse {

    private Long id;
    private String customerName;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private List<GarmentResponse> garments;

}
