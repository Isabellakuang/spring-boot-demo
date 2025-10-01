package com.java.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/test/path");
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Resource not found");
        assertThat(response.getBody().getPath()).isEqualTo("/test/path");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void shouldHandleMissingServletRequestParameterException() {
        // Given
        MissingServletRequestParameterException exception = 
            new MissingServletRequestParameterException("paramName", "String");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleMissingParameter(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Required request parameter 'paramName' is missing");
        assertThat(response.getBody().getPath()).isEqualTo("/test/path");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void shouldHandleGenericException() {
        // Given
        Exception exception = new Exception("Something went wrong");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleGeneric(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Something went wrong");
        assertThat(response.getBody().getPath()).isEqualTo("/test/path");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
