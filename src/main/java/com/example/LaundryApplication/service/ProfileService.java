package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.UserRepository;
import com.example.LaundryApplication.dto.request.UpdateProfileRequest;
import com.example.LaundryApplication.dto.response.ProfileResponse;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.transformer.ProfileTransformer;
import com.example.LaundryApplication.utility.Validation;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class ProfileService {

    private final Validation validation;
    private final UserRepository userRepository;

    public @Nullable ProfileResponse getProfile() {

        User user = validation.getCurrentUser();
        return ProfileTransformer.ProfileToProfileRespone(user);

    }

    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {

        User user = validation.getCurrentUser();

        if (request.getBusinessName() != null) {
            user.setBusinessName(request.getBusinessName());
        }

        if (request.getOwnerName() != null) {
            user.setOwnerName(request.getOwnerName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }

        if (request.getAboutBusiness() != null) {
            user.setAboutBusiness(request.getAboutBusiness());
        }

        if (request.getPanNumber() != null) {
            user.setPanNumber(request.getPanNumber());
        }

        if (request.getGstNumber() != null) {
            user.setGstNumber(request.getGstNumber());
        }

        userRepository.save(user);

        return getProfile();
    }
}
