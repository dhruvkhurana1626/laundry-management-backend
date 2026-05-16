package com.example.LaundryApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/health")
public class HealthCheckController {

    @GetMapping
    public String getHealth(){
        return "Server is running";
    }

}
