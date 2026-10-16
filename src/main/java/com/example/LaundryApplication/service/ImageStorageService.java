package com.example.LaundryApplication.service;

import com.example.LaundryApplication.ecxeption.BusinessException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image cannot be empty");
        }

        byte[] bytes;

        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image", e);
        }

        String detectedType = new Tika().detect(bytes);

        String extension = switch (detectedType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> throw new BusinessException(
                    "Only JPEG and PNG images are allowed"
            );
        };

        String fileName = "profile-images/"
                + UUID.randomUUID()
                + extension;

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(detectedType)
                        .build();

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(bytes)
            );
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload profile image", e);
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
    //to extract the key
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

    //helper method
    //to validate the actual type of content
    private String validateAndGetExtension(MultipartFile file)
            throws IOException {

        if (file.isEmpty()) {
            throw new BusinessException("Image cannot be empty");
        }

        String detectedType = new Tika().detect(file.getInputStream());

        return switch (detectedType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new BusinessException("Invalid image type");
        };
    }
}