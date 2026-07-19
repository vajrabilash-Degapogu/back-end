package com.qr_vehicle.QRvehicle.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import com.qr_vehicle.QRvehicle.entity.TagInventory;

public interface TagInventoryRepository extends JpaRepository<TagInventory, Long> {

    Optional<TagInventory> findByTagId(String tagId);

    Optional<TagInventory> findByUniqueCode(String uniqueCode);

    long countBySheetCodeAndAssignedFalse(String sheetCode);

    long countBySheetCodeAndAssignedTrue(String sheetCode);

    List<TagInventory> findByAssignedFalse();

    List<TagInventory> findByBatchCode(String batchCode);

    List<TagInventory> findBySheetCode(String sheetCode);

List<TagInventory> findByBatchCodeOrderBySheetNumberAscPairIndexAsc(String batchCode);

long countByAssigned(boolean assigned);
    

    long countByAssignedFalse();

    long countByAssignedTrue();

}