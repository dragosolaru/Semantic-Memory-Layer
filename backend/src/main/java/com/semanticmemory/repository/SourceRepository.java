package com.semanticmemory.repository;

import com.semanticmemory.model.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SourceRepository extends JpaRepository<Source, UUID> {
}