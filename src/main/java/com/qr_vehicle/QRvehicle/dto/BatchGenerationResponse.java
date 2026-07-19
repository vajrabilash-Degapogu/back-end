package com.qr_vehicle.QRvehicle.dto;

public class BatchGenerationResponse {

    private String batchCode;

    private Integer vehicles;

    private Integer stickers;

    public BatchGenerationResponse() {
    }

    public BatchGenerationResponse(String batchCode,
                                   Integer vehicles,
                                   Integer stickers) {

        this.batchCode = batchCode;
        this.vehicles = vehicles;
        this.stickers = stickers;

    }

    public String getBatchCode() {
        return batchCode;
    }

    public Integer getVehicles() {
        return vehicles;
    }

    public Integer getStickers() {
        return stickers;
    }
}