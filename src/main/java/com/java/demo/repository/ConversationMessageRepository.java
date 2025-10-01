package com.java.demo.repository;

import com.java.demo.model.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
}