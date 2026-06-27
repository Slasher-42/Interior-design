package com.ced.Vendor.Inventory.Service.repository;

import com.ced.Vendor.Inventory.Service.domain.PurchaseOrder;
import com.ced.Vendor.Inventory.Service.domain.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    List<PurchaseOrder> findByProjectId(UUID projectId);

    List<PurchaseOrder> findByProjectIdAndStatus(UUID projectId, PurchaseOrderStatus status);

    List<PurchaseOrder> findByVendorId(UUID vendorId);
}
