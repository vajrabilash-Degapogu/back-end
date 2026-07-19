package com.qr_vehicle.QRvehicle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.qr_vehicle.QRvehicle.entity.TagBatch;


public interface TagBatchRepository extends JpaRepository<TagBatch, Long> {

    Optional<TagBatch> findByBatchCode(String batchCode);

}