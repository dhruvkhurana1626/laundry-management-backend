package com.example.LaundryApplication.model;

import com.example.LaundryApplication.enums.GarmentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder

public class Garment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GarmentType type;

    private int quantity;

    private BigDecimal pricePerItem;

    @ManyToOne
    @JoinColumn(name = "orderEntity_id")
    private OrderEntity order;

}
