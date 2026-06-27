package com.ced.Document.Portfolio.Service.service;

import com.ced.Document.Portfolio.Service.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class DocumentStorageService {

    @Value("${app.documents.storage-dir:./document-storage}")
    private String storageDir;

    public String store(UUID documentId, int versionNumber, MultipartFile file) {
        try {
            Path dir = Path.of(storageDir, documentId.toString());
            Files.createDirectories(dir);
            Path target = dir.resolve(versionNumber + "_" + sanitize(file.getOriginalFilename()));
            file.transferTo(target);
            return target.toString();
        } catch (IOException e) {
            throw new AppException("Failed to store document file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] read(String storagePath) {
        try {
            return Files.readAllBytes(Path.of(storagePath));
        } catch (IOException e) {
            throw new AppException("Document file not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Strips path separators and other characters from the client-supplied original filename
     * so it cannot be used for directory traversal when building the storage path.
     */
    private String sanitize(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }
        return originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
