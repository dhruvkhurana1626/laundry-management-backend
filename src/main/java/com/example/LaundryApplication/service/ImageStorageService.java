package com.example.LaundryApplication.service;

import com.example.LaundryApplication.ecxeption.BusinessException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadProfileImage(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(
                    originalFilename.lastIndexOf(".")
            );
        }

        String fileName = "profile-images/"
                + UUID.randomUUID()
                + extension;

        try {

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to upload profile image",
                    e
            );
        }

        return "https://" + bucketName
                + ".s3." + getRegion()
                + ".amazonaws.com/"
                + fileName;
    }

    private String getRegion() {
        return s3Client.serviceClientConfiguration()
                .region()
                .id();
    }

    public void deleteProfileImage(String imageUrl) {

        String key = extractKeyFromUrl(imageUrl);

        DeleteObjectRequest deleteObjectRequest =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    //Helper Method
    private String extractKeyFromUrl(String imageUrl) {

        String prefix =
                "https://" + bucketName + ".s3."
                        + getRegion()
                        + ".amazonaws.com/";

        if (!imageUrl.startsWith(prefix)) {
            throw new BusinessException(
                    "Invalid profile image URL"
            );
        }

        return imageUrl.substring(prefix.length());
    }
}