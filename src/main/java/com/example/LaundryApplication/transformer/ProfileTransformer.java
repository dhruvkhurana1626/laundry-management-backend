package com.example.LaundryApplication.transformer;

import com.example.LaundryApplication.dto.response.ProfileResponse;
import com.example.LaundryApplication.model.User;

public class ProfileTransformer {

    public static ProfileResponse ProfileToProfileRespone(User user){

        return ProfileResponse.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .businessName(user.getBusinessName())
                .ownerName(user.getOwnerName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .aboutBusiness(user.getAboutBusiness())
                .profileImageUrl(user.getProfileImageUrl())
                .panNumber(user.getPanNumber())
                .gstNumber(user.getGstNumber())
                .build();
    }

}
