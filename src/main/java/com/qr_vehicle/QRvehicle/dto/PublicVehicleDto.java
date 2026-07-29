package com.qr_vehicle.QRvehicle.dto;

import lombok.Data;

@Data
public class PublicVehicleDto {

    private String ownerName;

    private String vehicleNumber;

    // Only used by frontend to decide whether
    // to show the Emergency Contact button.
    // Never displayed to the user.
    private String emergencyPhone;

}