package com.scb.ragdemo.exception;

import com.scb.ragdemo.exception.custom.DocumentNotFoundException;
import com.scb.ragdemo.exception.custom.InvalidFileException;
import com.scb.ragdemo.exception.custom.PoeApiException;
import com.scb.ragdemo.model.dto.response.ApiResponse;
import com.scb.ragdemo.model.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Handles all exceptions uniformly and returns standardized error responses
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle document not found exception
     */
    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleDocumentNotFound(DocumentNotFoundException ex) {
        log.error("Document not found exception: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage(), String.valueOf(ErrorCode.DOCUMENT_NOT_FOUND.getCode())));
    }

    /**
     * Handle invalid file exception
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFile(InvalidFileException ex) {
        log.error("Invalid file exception: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(), String.valueOf(ErrorCode.INVALID_FILE_TYPE.getCode())));
    }

    /**
     * Handle Poe API exception
     */
    @ExceptionHandler(PoeApiException.class)
    public ResponseEntity<ApiResponse<Void>> handlePoeApiException(PoeApiException ex) {
        log.error("Poe API exception: {}, status code: {}", ex.getMessage(), ex.getStatusCode());
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ApiResponse.error(ex.getMessage(), String.valueOf(ErrorCode.POE_API_ERROR.getCode())));
    }

    /**
     * Handle parameter validation exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Parameter validation failed: {}", errors);
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Parameter validation failed")
                .errorCode(String.valueOf(ErrorCode.INVALID_PARAMETER.getCode()))
                .data(errors)
                .build();
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * Handle file size exceeds limit exception
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.error("File size exceeds limit: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("File size exceeds limit", String.valueOf(ErrorCode.FILE_TOO_LARGE.getCode())));
    }

    /**
     * Handle illegal argument exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage(), String.valueOf(ErrorCode.INVALID_PARAMETER.getCode())));
    }

    /**
     * Handle all unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Internal server error", String.valueOf(ErrorCode.INTERNAL_ERROR.getCode())));
    }
}
