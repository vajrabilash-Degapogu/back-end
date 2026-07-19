package com.qr_vehicle.QRvehicle.dto;

public class GenerateBatchRequest {

    private String stickerType;

    private Integer vehicleCount;

    public GenerateBatchRequest() {
    }

    public String getStickerType() {
        return stickerType;
    }

    public void setStickerType(String stickerType) {
        this.stickerType = stickerType;
    }

    public Integer getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Integer vehicleCount) {
        this.vehicleCount = vehicleCount;
    }
}