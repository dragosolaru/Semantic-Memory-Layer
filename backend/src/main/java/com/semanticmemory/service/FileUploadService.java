package com.semanticmemory.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.directory:./uploads/profile-images}")
    private String uploadDirectory;
    
    @Value("${app.upload.max-size:5242880}")
    private long maxFileSize;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDirectory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Created upload directory: " + uploadDirectory);
            }
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }

    public String uploadProfileImage(MultipartFile file, String userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File too large. Max size: " + maxFileSize);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Not an image file");
        }

        String extension = getExtension(contentType);
        String fileName = userId + "_" + UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDirectory, fileName);

        Files.write(filePath, file.getBytes());

        return "uploads/profile-images/" + fileName;
    }

    public void deleteProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String fileName = imageUrl.contains("/") 
                ? imageUrl.substring(imageUrl.lastIndexOf('/') + 1)
                : imageUrl;
            Path filePath = Paths.get(uploadDirectory, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
    }

    public void deleteOldImageIfExists(String oldImageUrl) {
        if (oldImageUrl != null && (oldImageUrl.contains("uploads/") || oldImageUrl.contains("/api/uploads/"))) {
            deleteProfileImage(oldImageUrl);
        }
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }
}