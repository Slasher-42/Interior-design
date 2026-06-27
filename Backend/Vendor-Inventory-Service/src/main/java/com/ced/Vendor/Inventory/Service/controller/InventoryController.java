package com.ced.Vendor.Inventory.Service.controller;

import com.ced.Vendor.Inventory.Service.dto.InventoryItemResponse;
import com.ced.Vendor.Inventory.Service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/projects/{projectId}/inventory")
    public ResponseEntity<List<InventoryItemResponse>> listByProject(@PathVariable UUID projectId) {
        List<InventoryItemResponse> result = inventoryService.listByProject(projectId).stream()
                .map(InventoryItemResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }
}
