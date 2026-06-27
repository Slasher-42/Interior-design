package com.ced.Vendor.Inventory.Service.service;

import com.ced.Vendor.Inventory.Service.domain.PurchaseOrder;
import com.ced.Vendor.Inventory.Service.domain.PurchaseOrderStatus;
import com.ced.Vendor.Inventory.Service.domain.Vendor;
import com.ced.Vendor.Inventory.Service.dto.CreateVendorRequest;
import com.ced.Vendor.Inventory.Service.dto.UpdateVendorRequest;
import com.ced.Vendor.Inventory.Service.dto.VendorPerformanceResponse;
import com.ced.Vendor.Inventory.Service.exception.AppException;
import com.ced.Vendor.Inventory.Service.repository.PurchaseOrderRepository;
import com.ced.Vendor.Inventory.Service.repository.VendorRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public Vendor create(CreateVendorRequest request) {
        Vendor vendor = Vendor.builder()
                .name(request.name())
                .contactName(request.contactName())
                .email(request.email())
                .phone(request.phone())
                .suppliedMaterials(request.suppliedMaterials() != null ? request.suppliedMaterials() : List.of())
                .build();
        return vendorRepository.save(vendor);
    }

    @Transactional
    public Vendor update(UUID id, UpdateVendorRequest request) {
        Vendor vendor = getById(id);
        if (request.name() != null) {
            vendor.setName(request.name());
        }
        if (request.contactName() != null) {
            vendor.setContactName(request.contactName());
        }
        if (request.email() != null) {
            vendor.setEmail(request.email());
        }
        if (request.phone() != null) {
            vendor.setPhone(request.phone());
        }
        if (request.suppliedMaterials() != null) {
            vendor.setSuppliedMaterials(request.suppliedMaterials());
        }
        if (request.active() != null) {
            vendor.setActive(request.active());
        }
        return vendorRepository.save(vendor);
    }

    public Vendor getById(UUID id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new AppException("Vendor not found", HttpStatus.NOT_FOUND));
    }

    public Page<Vendor> search(String query, String material, Boolean active, Pageable pageable) {
        Specification<Vendor> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(query)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%"));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (StringUtils.hasText(material)) {
                cq.distinct(true);
                Join<Vendor, String> materialsJoin = root.join("suppliedMaterials");
                predicates.add(cb.equal(cb.lower(materialsJoin), material.toLowerCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return vendorRepository.findAll(spec, pageable);
    }

    public List<Vendor> findMatchingVendors(List<String> materials) {
        if (materials == null || materials.isEmpty()) {
            return List.of();
        }
        List<String> lowerCased = materials.stream().map(String::toLowerCase).toList();
        return vendorRepository.findBySuppliedMaterialIn(lowerCased);
    }

    public VendorPerformanceResponse performance(UUID vendorId) {
        getById(vendorId);
        List<PurchaseOrder> orders = purchaseOrderRepository.findByVendorId(vendorId);
        List<PurchaseOrder> received = orders.stream()
                .filter(o -> o.getStatus() == PurchaseOrderStatus.RECEIVED)
                .toList();

        double onTimeRate = received.isEmpty() ? 0.0 : received.stream()
                .filter(o -> o.getExpectedDeliveryDate() != null && o.getActualDeliveryDate() != null
                        && !o.getActualDeliveryDate().isAfter(o.getExpectedDeliveryDate()))
                .count() / (double) received.size();

        double accuracyRate = received.isEmpty() ? 0.0 : received.stream()
                .filter(o -> Boolean.TRUE.equals(o.getAccurate()))
                .count() / (double) received.size();

        OptionalDouble avgCostVariance = received.stream()
                .filter(o -> o.getActualCost() != null && o.getEstimatedCost() != null
                        && o.getEstimatedCost().signum() != 0)
                .mapToDouble(o -> o.getActualCost().subtract(o.getEstimatedCost())
                        .divide(o.getEstimatedCost(), 4, RoundingMode.HALF_UP)
                        .doubleValue() * 100)
                .average();

        OptionalDouble avgDelayDays = received.stream()
                .filter(o -> o.getExpectedDeliveryDate() != null && o.getActualDeliveryDate() != null)
                .mapToLong(o -> ChronoUnit.DAYS.between(o.getExpectedDeliveryDate(), o.getActualDeliveryDate()))
                .average();

        return VendorPerformanceResponse.builder()
                .vendorId(vendorId)
                .totalOrders(orders.size())
                .receivedOrders(received.size())
                .onTimeDeliveryRate(onTimeRate)
                .accuracyRate(accuracyRate)
                .averageCostVariancePercent(avgCostVariance.isPresent() ? avgCostVariance.getAsDouble() : null)
                .averageDeliveryDelayDays(avgDelayDays.isPresent() ? avgDelayDays.getAsDouble() : null)
                .build();
    }
}
