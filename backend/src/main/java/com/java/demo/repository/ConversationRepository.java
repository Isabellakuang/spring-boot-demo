package com.java.demo.repository;

import com.java.demo.model.Conversation;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @EntityGraph(attributePaths = "messages")
    Optional<Conversation> findWithMessagesById(Long id);
}