package com.qr_vehicle.QRvehicle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qr_vehicle.QRvehicle.entity.TagSheet;

public interface TagSheetRepository extends JpaRepository<TagSheet, Long> {

    List<TagSheet> findByBatchCode(String batchCode);

    Optional<TagSheet> findBySheetCode(String sheetCode);

}