package com.ced.Vendor.Inventory.Service.service;

import com.ced.Vendor.Inventory.Service.domain.MaterialLineItem;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequest;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequestStatus;
import com.ced.Vendor.Inventory.Service.domain.Vendor;
import com.ced.Vendor.Inventory.Service.dto.CreateMaterialRequestRequest;
import com.ced.Vendor.Inventory.Service.exception.AppException;
import com.ced.Vendor.Inventory.Service.repository.MaterialRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialRequestService {

    private final MaterialRequestRepository materialRequestRepository;
    private final VendorService vendorService;

    @Transactional
    public MaterialRequest create(UUID projectId, UUID requestedBy, CreateMaterialRequestRequest request) {
        List<MaterialLineItem> items = request.items().stream()
                .map(i -> MaterialLineItem.builder().materialName(i.materialName()).quantity(i.quantity()).build())
                .toList();

        MaterialRequest materialRequest = MaterialRequest.builder()
                .projectId(projectId)
                .requestedBy(requestedBy)
                .items(items)
                .build();
        return materialRequestRepository.save(materialRequest);
    }

    public MaterialRequest getById(UUID id) {
        return materialRequestRepository.findById(id)
                .orElseThrow(() -> new AppException("Material request not found", HttpStatus.NOT_FOUND));
    }

    public List<MaterialRequest> listByProject(UUID projectId, MaterialRequestStatus status) {
        return status != null
                ? materialRequestRepository.findByProjectIdAndStatus(projectId, status)
                : materialRequestRepository.findByProjectId(projectId);
    }

    public List<Vendor> findMatchingVendors(UUID materialRequestId) {
        MaterialRequest materialRequest = getById(materialRequestId);
        List<String> materialNames = materialRequest.getItems().stream()
                .map(MaterialLineItem::getMaterialName)
                .toList();
        return vendorService.findMatchingVendors(materialNames);
    }

    @Transactional
    void markFulfilled(UUID id) {
        MaterialRequest materialRequest = getById(id);
        materialRequest.setStatus(MaterialRequestStatus.FULFILLED);
        materialRequestRepository.save(materialRequest);
    }
}
