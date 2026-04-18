package com.example.LaundryApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

@SpringBootApplication
@ControllerAdvice
public class LaundryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaundryApplication.class, args);
	}

}
