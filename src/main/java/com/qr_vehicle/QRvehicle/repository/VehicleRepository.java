package com.qr_vehicle.QRvehicle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qr_vehicle.QRvehicle.entity.VehicleOwner;

public interface VehicleRepository extends JpaRepository<VehicleOwner, Long> {
    Optional<VehicleOwner> findByUniqueCode(String uniqueCode);


}
