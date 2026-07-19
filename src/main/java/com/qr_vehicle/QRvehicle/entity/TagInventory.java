package com.qr_vehicle.QRvehicle.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tag_inventory")
public class TagInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 8)
    private String tagId;

    @Column(unique = true, nullable = false)
    private String uniqueCode;

    @Column(nullable = false)
    private String batchCode;

    @Column(nullable = false)
    private String stickerType;

    private Boolean assigned = false;

    private Long orderId;

    private Long vehicleId;

    private LocalDateTime assignedAt;

    // Sheet Position
    private Integer sheetRow;

    private Integer sheetColumn;

    private Integer pairIndex;

    public TagInventory() {
    }

    public Long getId() {
        return id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getStickerType() {
        return stickerType;
    }

    public void setStickerType(String stickerType) {
        this.stickerType = stickerType;
    }

    public Boolean getAssigned() {
        return assigned;
    }

    public void setAssigned(Boolean assigned) {
        this.assigned = assigned;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Integer getSheetRow() {
        return sheetRow;
    }

    public void setSheetRow(Integer sheetRow) {
        this.sheetRow = sheetRow;
    }

    public Integer getSheetColumn() {
        return sheetColumn;
    }

    public void setSheetColumn(Integer sheetColumn) {
        this.sheetColumn = sheetColumn;
    }

    public Integer getPairIndex() {
        return pairIndex;
    }

    public void setPairIndex(Integer pairIndex) {
        this.pairIndex = pairIndex;
    }

    @Column(nullable = false)
private String sheetCode;

public String getSheetCode() {
        return sheetCode;
    }

    public void setSheetCode(String sheetCode) {
        this.sheetCode = sheetCode;
    }

    public Integer getSheetNumber() {
        return sheetNumber;
    }

    public void setSheetNumber(Integer sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

@Column(nullable = false)
private Integer sheetNumber;

}