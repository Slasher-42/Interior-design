package com.ced.Vendor.Inventory.Service.repository;

import com.ced.Vendor.Inventory.Service.domain.MaterialRequest;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, UUID> {

    List<MaterialRequest> findByProjectId(UUID projectId);

    List<MaterialRequest> findByProjectIdAndStatus(UUID projectId, MaterialRequestStatus status);
}
