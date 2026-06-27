package com.ced.Vendor.Inventory.Service.service;

import com.ced.Vendor.Inventory.Service.domain.InventoryItem;
import com.ced.Vendor.Inventory.Service.domain.MaterialLineItem;
import com.ced.Vendor.Inventory.Service.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    @Transactional
    public void receiveMaterials(UUID projectId, List<MaterialLineItem> items) {
        for (MaterialLineItem item : items) {
            InventoryItem inventoryItem = inventoryItemRepository
                    .findByProjectIdAndMaterialNameIgnoreCase(projectId, item.getMaterialName())
                    .orElseGet(() -> InventoryItem.builder()
                            .projectId(projectId)
                            .materialName(item.getMaterialName())
                            .quantityOnHand(0)
                            .build());
            inventoryItem.setQuantityOnHand(inventoryItem.getQuantityOnHand() + item.getQuantity());
            inventoryItemRepository.save(inventoryItem);
        }
    }

    public List<InventoryItem> listByProject(UUID projectId) {
        return inventoryItemRepository.findByProjectId(projectId);
    }
}
