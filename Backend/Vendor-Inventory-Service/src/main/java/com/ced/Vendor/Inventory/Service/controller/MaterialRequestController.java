package com.ced.Vendor.Inventory.Service.controller;

import com.ced.Vendor.Inventory.Service.domain.MaterialRequestStatus;
import com.ced.Vendor.Inventory.Service.dto.CreateMaterialRequestRequest;
import com.ced.Vendor.Inventory.Service.dto.MaterialRequestResponse;
import com.ced.Vendor.Inventory.Service.dto.VendorResponse;
import com.ced.Vendor.Inventory.Service.security.CurrentUser;
import com.ced.Vendor.Inventory.Service.service.MaterialRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MaterialRequestController {

    private final MaterialRequestService materialRequestService;

    @PostMapping("/projects/{projectId}/material-requests")
    public ResponseEntity<MaterialRequestResponse> create(@PathVariable UUID projectId,
                                                           @Valid @RequestBody CreateMaterialRequestRequest request) {
        var materialRequest = materialRequestService.create(projectId, CurrentUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(MaterialRequestResponse.from(materialRequest));
    }

    @GetMapping("/projects/{projectId}/material-requests")
    public ResponseEntity<List<MaterialRequestResponse>> listByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) MaterialRequestStatus status) {
        List<MaterialRequestResponse> result = materialRequestService.listByProject(projectId, status).stream()
                .map(MaterialRequestResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/material-requests/{id}")
    public ResponseEntity<MaterialRequestResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(MaterialRequestResponse.from(materialRequestService.getById(id)));
    }

    @GetMapping("/material-requests/{id}/matching-vendors")
    public ResponseEntity<List<VendorResponse>> matchingVendors(@PathVariable UUID id) {
        List<VendorResponse> result = materialRequestService.findMatchingVendors(id).stream()
                .map(VendorResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }
}
