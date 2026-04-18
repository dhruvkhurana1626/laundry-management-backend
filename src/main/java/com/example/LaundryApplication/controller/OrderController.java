package com.example.LaundryApplication.controller;

import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity createOrder(@RequestBody @Valid OrderRequest orderRequest){
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @GetMapping
    public List<OrderResponse> getOrder()


}
