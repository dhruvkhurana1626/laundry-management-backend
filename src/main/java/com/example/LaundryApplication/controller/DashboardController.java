package com.example.LaundryApplication.controller;

import com.example.LaundryApplication.dto.response.DashboardResponse;
import com.example.LaundryApplication.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dashboard")
public class DashboardController {

    private final OrderService orderService;

    @GetMapping
    public DashboardResponse getDashboard() {
        return orderService.getDashboard();
    }

}
