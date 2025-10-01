package com.java.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateConversationRequest {

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    private String customerEmail;

    @NotBlank(message = "Initial message is required")
    private String initialMessage;

    public String getSubject() {
        return subject;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getInitialMessage() {
        return initialMessage;
    }
}
