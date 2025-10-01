package com.java.demo.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "faq_entries")
public class FaqEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, length = 3000)
    private String answer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "faq_tags", joinColumns = @JoinColumn(name = "faq_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    public FaqEntry() {
    }

    public FaqEntry(String question, String answer, Set<String> tags) {
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

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaqEntry faqEntry)) return false;
        return Objects.equals(id, faqEntry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}