package com.java.demo.repository;

import com.java.demo.model.FaqEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqEntry, Long> {
}
