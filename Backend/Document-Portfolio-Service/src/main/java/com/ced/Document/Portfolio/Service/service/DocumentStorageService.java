package com.ced.Document.Portfolio.Service.service;

import com.ced.Document.Portfolio.Service.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class DocumentStorageService {

    private final S3Client s3Client;
    private final String bucket;

    public DocumentStorageService(@Value("${app.aws.s3.bucket}") String bucket,
                                   @Value("${app.aws.s3.region}") String region,
                                   @Value("${app.aws.access-key-id}") String accessKeyId,
                                   @Value("${app.aws.secret-access-key}") String secretAccessKey) {
        this.bucket = bucket;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    public String store(UUID documentId, int versionNumber, MultipartFile file) {
        String key = "documents/" + documentId + "/" + versionNumber + "_" + sanitize(file.getOriginalFilename());
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;
        } catch (IOException e) {
            throw new AppException("Failed to store document file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] read(String storagePath) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(storagePath)
                            .build())
                    .readAllBytes();
        } catch (IOException e) {
            throw new AppException("Document file not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Strips path separators and other characters from the client-supplied original filename
     * so it cannot be used for directory traversal when building the S3 object key.
     */
    private String sanitize(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }
        return originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
