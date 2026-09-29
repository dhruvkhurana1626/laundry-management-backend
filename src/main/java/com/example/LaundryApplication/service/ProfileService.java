package com.example.LaundryApplication.service;

import com.example.LaundryApplication.dao.UserRepository;
import com.example.LaundryApplication.dto.request.UpdateProfileRequest;
import com.example.LaundryApplication.dto.response.ProfileResponse;
import com.example.LaundryApplication.ecxeption.BusinessException;
import com.example.LaundryApplication.model.User;
import com.example.LaundryApplication.transformer.ProfileTransformer;
import com.example.LaundryApplication.utility.Validation;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor

public class ProfileService {

    private final Validation validation;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

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

    public ProfileResponse updateProfileImage(MultipartFile file) {

        User user = validation.getCurrentUser();

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Profile image is required");
        }

        String contentType = file.getContentType();

        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)) {
            throw new BusinessException(
                    "Only JPEG and PNG images are allowed"
            );
        }

        String imageUrl = imageStorageService.uploadProfileImage(file);

        user.setProfileImageUrl(imageUrl);

        userRepository.save(user);

        return getProfile();
    }

    public ProfileResponse deleteProfileImage() {

        User user = validation.getCurrentUser();

        if (user.getProfileImageUrl() == null) {
            throw new BusinessException(
                    "No profile image to remove"
            );
        }

        imageStorageService.deleteProfileImage(
                user.getProfileImageUrl()
        );

        user.setProfileImageUrl(null);

        userRepository.save(user);

        return getProfile();
    }
}
