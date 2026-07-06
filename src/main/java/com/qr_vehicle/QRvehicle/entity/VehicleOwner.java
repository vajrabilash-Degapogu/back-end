package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class VehicleOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String ownerName;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String emergencyName;

    @NotBlank
    private String emergencyPhone;

    @NotBlank
    private String vehicleNumber;

    @NotBlank
    private String address;

    @Column(unique = true)
    private String uniqueCode;
}
