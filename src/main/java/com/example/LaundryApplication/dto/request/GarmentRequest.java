package com.example.LaundryApplication.dto.request;

import com.example.LaundryApplication.enums.GarmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class GarmentRequest {

    @NotNull
    private GarmentType type;

    @NotNull
    @Min(1)
    private Integer quantity;
}
