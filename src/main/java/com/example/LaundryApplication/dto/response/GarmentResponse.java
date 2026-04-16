package com.example.LaundryApplication.dto.response;

import com.example.LaundryApplication.enums.GarmentType;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class GarmentResponse {

    private GarmentType type;
    private int quantity;
    private BigDecimal pricePerItem;

}
