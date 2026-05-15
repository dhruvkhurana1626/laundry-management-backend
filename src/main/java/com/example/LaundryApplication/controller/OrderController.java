package com.example.LaundryApplication.controller;

import com.example.LaundryApplication.dto.request.OrderRequest;
import com.example.LaundryApplication.dto.response.OrderResponse;
import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        origins = "https://laundry-management-frontend.vercel.app"
)
@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity createOrder(@RequestBody @Valid OrderRequest orderRequest){
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    //loading all the orders
    @GetMapping("/recent")
    public List<OrderResponse> getRecentOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getRecentOrders(page, size);
    }

    //filtering the orders
    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestParam(required = false)
            OrderStatus status,
            @RequestParam(required = false)
            String search,
            @RequestParam(required = false)
            Integer days,
            @RequestParam(defaultValue = "0")
            int page
    ) {
        return orderService.getOrders(status, search, days, page);
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status
    ) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable String orderId){
        orderService.deleteOrder(orderId);
        return "Order Deleted SuccessFully";
    }


}
