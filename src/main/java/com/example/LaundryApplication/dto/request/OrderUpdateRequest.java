package com.example.LaundryApplication.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {

    private String customerName;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Invalid phone number"
    )
    private String phone;

    @Email
    private String email;

    private List<@Valid GarmentRequest> garmentRequestList;
}