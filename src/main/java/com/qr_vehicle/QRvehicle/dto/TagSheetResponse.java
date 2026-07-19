package com.qr_vehicle.QRvehicle.dto;

public class TagSheetResponse {

    private Long id;
    private String sheetCode;
    private String batchCode;
    private Integer sheetNumber;
    private Integer vehiclesCount;
    private Integer stickersCount;

    private Long availableTags;
    private Long assignedTags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSheetCode() {
        return sheetCode;
    }

    public void setSheetCode(String sheetCode) {
        this.sheetCode = sheetCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(Integer sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    public Integer getVehiclesCount() {
        return vehiclesCount;
    }

    public void setVehiclesCount(Integer vehiclesCount) {
        this.vehiclesCount = vehiclesCount;
    }

    public Integer getStickersCount() {
        return stickersCount;
    }

    public void setStickersCount(Integer stickersCount) {
        this.stickersCount = stickersCount;
    }

    public Long getAvailableTags() {
        return availableTags;
    }

    public void setAvailableTags(Long availableTags) {
        this.availableTags = availableTags;
    }

    public Long getAssignedTags() {
        return assignedTags;
    }

    public void setAssignedTags(Long assignedTags) {
        this.assignedTags = assignedTags;
    }
}