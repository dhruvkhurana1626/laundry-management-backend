package com.example.LaundryApplication.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateProfileRequest {

    private String businessName;
    private String ownerName;
    private String phone;
    private String address;
    private String city;
    private String aboutBusiness;

    private String panNumber;
    private String gstNumber;

}