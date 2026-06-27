package com.ced.Document.Portfolio.Service.repository;

import com.ced.Document.Portfolio.Service.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID>, JpaSpecificationExecutor<Document> {
}
