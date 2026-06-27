package com.ced.Vendor.Inventory.Service.repository;

import com.ced.Vendor.Inventory.Service.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    List<InventoryItem> findByProjectId(UUID projectId);

    Optional<InventoryItem> findByProjectIdAndMaterialNameIgnoreCase(UUID projectId, String materialName);
}
