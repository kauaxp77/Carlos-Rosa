package com.carlosrosa.portfolio.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MediaController {

    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${app.aws.s3.region}")
    private String regionString;

    @GetMapping("/presigned-url")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Map<String, String>> generatePreSignedUrl(@RequestParam String filename,
            @RequestParam String contentType) {

        // Security checks: restrict content types (Threat Model OWASP)
        if (!contentType.startsWith("image/") && !contentType.startsWith("video/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid MIME type. Only images and videos are permitted."));
        }

        // Generate a random UUID to avoid overwriting or guessing files in S3
        String objectKey = "originals/" + UUID.randomUUID().toString() + "_"
                + filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        Region region = Region.of(regionString);
        try (S3Presigner presigner = S3Presigner.builder().region(region).build()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // Valid only for 10 minutes (security)
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            Map<String, String> response = new HashMap<>();
            response.put("presignedUrl", presignedRequest.url().toString());
            response.put("objectKey", objectKey); // Save this locally to DB post-upload

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error generating S3 presigned URL: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
