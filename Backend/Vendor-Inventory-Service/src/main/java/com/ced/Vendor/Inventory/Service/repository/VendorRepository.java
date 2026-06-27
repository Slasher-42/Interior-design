package com.ced.Vendor.Inventory.Service.repository;

import com.ced.Vendor.Inventory.Service.domain.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID>, JpaSpecificationExecutor<Vendor> {

    @Query("select distinct v from Vendor v join v.suppliedMaterials m where lower(m) in :materials")
    List<Vendor> findBySuppliedMaterialIn(@Param("materials") List<String> materials);
}
