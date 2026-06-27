package com.ced.Vendor.Inventory.Service.service;

import com.ced.Vendor.Inventory.Service.domain.MaterialLineItem;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequest;
import com.ced.Vendor.Inventory.Service.domain.MaterialRequestStatus;
import com.ced.Vendor.Inventory.Service.domain.PurchaseOrder;
import com.ced.Vendor.Inventory.Service.domain.PurchaseOrderStatus;
import com.ced.Vendor.Inventory.Service.dto.CreatePurchaseOrderRequest;
import com.ced.Vendor.Inventory.Service.dto.ReceivePurchaseOrderRequest;
import com.ced.Vendor.Inventory.Service.event.KafkaEventPublisher;
import com.ced.Vendor.Inventory.Service.event.PurchaseOrderCreatedEvent;
import com.ced.Vendor.Inventory.Service.exception.AppException;
import com.ced.Vendor.Inventory.Service.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MaterialRequestService materialRequestService;
    private final VendorService vendorService;
    private final InventoryService inventoryService;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public PurchaseOrder create(UUID materialRequestId, CreatePurchaseOrderRequest request) {
        MaterialRequest materialRequest = materialRequestService.getById(materialRequestId);
        if (materialRequest.getStatus() != MaterialRequestStatus.PENDING) {
            throw new AppException("Only pending material requests can be fulfilled with a purchase order", HttpStatus.CONFLICT);
        }
        vendorService.getById(request.vendorId());

        PurchaseOrder order = PurchaseOrder.builder()
                .materialRequestId(materialRequestId)
                .projectId(materialRequest.getProjectId())
                .vendorId(request.vendorId())
                .items(materialRequest.getItems())
                .estimatedCost(request.estimatedCost())
                .expectedDeliveryDate(request.expectedDeliveryDate())
                .build();
        order = purchaseOrderRepository.save(order);

        materialRequestService.markFulfilled(materialRequestId);

        kafkaEventPublisher.publishPurchaseOrderCreated(PurchaseOrderCreatedEvent.builder()
                .orderId(order.getId())
                .projectId(order.getProjectId())
                .vendorId(order.getVendorId())
                .materials(order.getItems().stream().map(MaterialLineItem::getMaterialName).toList())
                .estimatedCost(order.getEstimatedCost())
                .createdAt(order.getCreatedAt())
                .build());

        return order;
    }

    @Transactional
    public PurchaseOrder receive(UUID id, ReceivePurchaseOrderRequest request) {
        PurchaseOrder order = getById(id);
        if (order.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new AppException("Only created purchase orders can be marked received", HttpStatus.CONFLICT);
        }
        order.setStatus(PurchaseOrderStatus.RECEIVED);
        order.setActualCost(request.actualCost());
        order.setActualDeliveryDate(request.actualDeliveryDate() != null ? request.actualDeliveryDate() : LocalDate.now());
        order.setAccurate(request.accurate());
        order = purchaseOrderRepository.save(order);

        inventoryService.receiveMaterials(order.getProjectId(), order.getItems());

        return order;
    }

    public PurchaseOrder getById(UUID id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("Purchase order not found", HttpStatus.NOT_FOUND));
    }

    public List<PurchaseOrder> listByProject(UUID projectId, PurchaseOrderStatus status) {
        return status != null
                ? purchaseOrderRepository.findByProjectIdAndStatus(projectId, status)
                : purchaseOrderRepository.findByProjectId(projectId);
    }
}
