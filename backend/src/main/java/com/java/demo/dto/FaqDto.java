package com.java.demo.dto;

import java.util.Set;

public class FaqDto {

    private final Long id;
    private final String question;
    private final String answer;
    private final Set<String> tags;

    public FaqDto(Long id, String question, String answer, Set<String> tags) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public Set<String> getTags() {
        return tags;
    }
}
