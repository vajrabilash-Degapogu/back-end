package com.qr_vehicle.QRvehicle.dto;

import lombok.Data;

@Data
public class PublicVehicleDto {

    private String ownerName;
    private String vehicleNumber;

    // Public flag and username only; phone numbers and share links remain server-side.
    private boolean whatsappAvailable;
    private String whatsappUsername;

    // Only a presence flag is exposed for emergency contact.
    private boolean emergencyContactAvailable;
}
