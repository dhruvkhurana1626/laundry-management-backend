package com.example.LaundryApplication.controller;

import com.example.LaundryApplication.dto.response.PricingResponse;
import com.example.LaundryApplication.enums.GarmentType;
import com.example.LaundryApplication.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/admin/pricing")
@RequiredArgsConstructor

public class PricingController {

    private final PricingService pricingService;

    @PutMapping("/{garmentType}")
    public ResponseEntity<Void> updatePrice(
            @PathVariable GarmentType garmentType,
            @RequestParam BigDecimal price
            ){

        pricingService.updatePrice(garmentType,price);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PricingResponse>> getPricing(){
        return ResponseEntity.ok(pricingService.getCurrentUserPricing());
    }
}
