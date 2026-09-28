package com.example.LaundryApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder

public class ProfileResponse {

    private String name;
    private String email;

    private String businessName;
    private String ownerName;
    private String phone;
    private String address;
    private String city;
    private String aboutBusiness;

    private String profileImageUrl;

    private String panNumber;
    private String gstNumber;

}