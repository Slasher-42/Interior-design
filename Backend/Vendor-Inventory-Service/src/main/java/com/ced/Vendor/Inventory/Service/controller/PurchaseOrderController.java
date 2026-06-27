package com.ced.Vendor.Inventory.Service.controller;

import com.ced.Vendor.Inventory.Service.domain.PurchaseOrderStatus;
import com.ced.Vendor.Inventory.Service.dto.CreatePurchaseOrderRequest;
import com.ced.Vendor.Inventory.Service.dto.PurchaseOrderResponse;
import com.ced.Vendor.Inventory.Service.dto.ReceivePurchaseOrderRequest;
import com.ced.Vendor.Inventory.Service.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/material-requests/{materialRequestId}/purchase-orders")
    public ResponseEntity<PurchaseOrderResponse> create(@PathVariable UUID materialRequestId,
                                                         @Valid @RequestBody CreatePurchaseOrderRequest request) {
        var order = purchaseOrderService.create(materialRequestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PurchaseOrderResponse.from(order));
    }

    @GetMapping("/projects/{projectId}/purchase-orders")
    public ResponseEntity<List<PurchaseOrderResponse>> listByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) PurchaseOrderStatus status) {
        List<PurchaseOrderResponse> result = purchaseOrderService.listByProject(projectId, status).stream()
                .map(PurchaseOrderResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrderResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.getById(id)));
    }

    @PatchMapping("/purchase-orders/{id}/receive")
    public ResponseEntity<PurchaseOrderResponse> receive(@PathVariable UUID id,
                                                          @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        return ResponseEntity.ok(PurchaseOrderResponse.from(purchaseOrderService.receive(id, request)));
    }
}
