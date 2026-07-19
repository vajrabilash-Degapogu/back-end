// package com.qr_vehicle.QRvehicle.repository;

// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;

// import com.qr_vehicle.QRvehicle.entity.VehicleOwner;

// public interface VehicleRepository extends JpaRepository<VehicleOwner, Long> {
//     Optional<VehicleOwner> findByUniqueCode(String uniqueCode);

// }

package com.qr_vehicle.QRvehicle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qr_vehicle.QRvehicle.entity.VehicleOwner;

public interface VehicleRepository extends JpaRepository<VehicleOwner, Long> {

    // Find by QR unique code
    Optional<VehicleOwner> findByUniqueCode(String uniqueCode);

    // Check duplicate vehicle number
    Optional<VehicleOwner> findByVehicleNumber(String vehicleNumber);

    // Check duplicate phone number (optional but recommended)
    Optional<VehicleOwner> findByPhoneNumber(String phoneNumber);

    

    // Exists checks (faster than fetching the whole entity)
    boolean existsByVehicleNumber(String vehicleNumber);

    
}