package com.example.LaundryApplication.dto.response;

import com.example.LaundryApplication.enums.GarmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class PricingResponse {

    private GarmentType garmentType;
    private BigDecimal price;

}

