package com.semanticmemory.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    @Value("${app.upload.directory:./uploads/profile-images}")
    private String uploadDirectory;

    @GetMapping("/profile-images/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDirectory).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = getContentType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            // File not found
        }
        return ResponseEntity.notFound().build();
    }

    private String getContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}