package com.scb.ragdemo.util;

import com.scb.ragdemo.exception.custom.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "txt");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "application/pdf",
        "text/plain"
    );

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be empty");
        }

        validateFileName(file.getOriginalFilename());
        validateFileSize(file.getSize());
        validateFileType(file);
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new InvalidFileException("File name cannot be empty");
        }

        if (fileName.contains("..")) {
            throw new InvalidFileException("File name contains illegal characters");
        }

        String extension = getFileExtension(fileName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(
                String.format("Unsupported file type: %s, only supports: %s", 
                    extension, String.join(", ", ALLOWED_EXTENSIONS))
            );
        }
    }

    private void validateFileSize(long size) {
        if (size <= 0) {
            throw new InvalidFileException("Invalid file size");
        }

        if (size > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                String.format("File size exceeds limit: %.2f MB, maximum allowed: %.2f MB",
                    size / (1024.0 * 1024.0),
                    MAX_FILE_SIZE / (1024.0 * 1024.0))
            );
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                String.format("Unsupported file MIME type: %s", contentType)
            );
        }
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public boolean isValidPdfFile(MultipartFile file) {
        try {
            validateFile(file);
            return true;
        } catch (InvalidFileException e) {
            log.warn("File validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed";
        }
        
        return fileName
            .replaceAll("[^a-zA-Z0-9\\.\\-_]", "_")
            .replaceAll("_{2,}", "_")
            .trim();
    }
}
