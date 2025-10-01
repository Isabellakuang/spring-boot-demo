package com.java.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {

    @NotBlank(message = "Sender must be provided")
    private String sender;

    @NotBlank(message = "Content must not be blank")
    private String content;

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageRequest() {
    }

    public MessageRequest(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
}
