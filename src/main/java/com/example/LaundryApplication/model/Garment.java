package com.example.LaundryApplication.model;

import com.example.LaundryApplication.enums.GarmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
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
