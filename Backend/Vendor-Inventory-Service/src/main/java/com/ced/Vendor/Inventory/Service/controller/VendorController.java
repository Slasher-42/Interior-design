package com.ced.Vendor.Inventory.Service.controller;

import com.ced.Vendor.Inventory.Service.dto.CreateVendorRequest;
import com.ced.Vendor.Inventory.Service.dto.UpdateVendorRequest;
import com.ced.Vendor.Inventory.Service.dto.VendorPerformanceResponse;
import com.ced.Vendor.Inventory.Service.dto.VendorResponse;
import com.ced.Vendor.Inventory.Service.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponse> create(@Valid @RequestBody CreateVendorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(VendorResponse.from(vendorService.create(request)));
    }

    @GetMapping
    public ResponseEntity<Page<VendorResponse>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<VendorResponse> result = vendorService.search(query, material, active, PageRequest.of(page, size))
                .map(VendorResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(VendorResponse.from(vendorService.getById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendorResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateVendorRequest request) {
        return ResponseEntity.ok(VendorResponse.from(vendorService.update(id, request)));
    }

    @GetMapping("/{id}/performance")
    public ResponseEntity<VendorPerformanceResponse> performance(@PathVariable UUID id) {
        return ResponseEntity.ok(vendorService.performance(id));
    }
}
