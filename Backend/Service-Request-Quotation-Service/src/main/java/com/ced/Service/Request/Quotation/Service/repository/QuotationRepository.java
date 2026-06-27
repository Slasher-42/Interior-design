package com.ced.Service.Request.Quotation.Service.repository;

import com.ced.Service.Request.Quotation.Service.domain.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface QuotationRepository extends JpaRepository<Quotation, UUID>, JpaSpecificationExecutor<Quotation> {

    List<Quotation> findByRequestIdOrderByCreatedAtDesc(UUID requestId);
}
