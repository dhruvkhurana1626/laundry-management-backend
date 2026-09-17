package com.example.LaundryApplication.model;

import com.example.LaundryApplication.enums.GarmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "pricing",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "garment_type"}
                )
        }
)
@Getter
@Setter
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GarmentType garmentType;

    @Column(nullable = false)
    private BigDecimal price;

}