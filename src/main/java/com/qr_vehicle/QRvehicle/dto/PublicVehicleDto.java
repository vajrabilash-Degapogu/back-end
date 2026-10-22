package com.qr_vehicle.QRvehicle.dto;

import lombok.Data;

@Data
public class PublicVehicleDto {

    private String ownerName;
    private String vehicleNumber;

    // Public flag only. The public contact page should not receive phone numbers.
    private boolean whatsappAvailable;
    private String whatsappUsername;
    private String whatsappUsernameLink;

    // Existing UI currently uses this only to decide whether to show the
    // emergency button. A later privacy hardening change should replace this
    // with a boolean so the actual emergency number is not sent publicly.
    private String emergencyPhone;
}
