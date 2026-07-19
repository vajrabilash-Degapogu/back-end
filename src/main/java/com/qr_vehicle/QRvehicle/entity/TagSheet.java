package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tag_sheet")
public class TagSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sheetCode;

    @Column(nullable = false)
    private String batchCode;

    @Column(nullable = false)
    private Integer sheetNumber;

    @Column(nullable = false)
    private Integer vehiclesCount;

    @Column(nullable = false)
    private Integer stickersCount;

    private Boolean printed = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public TagSheet() {
    }

    public Long getId() {
        return id;
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

    public Boolean getPrinted() {
        return printed;
    }

    public void setPrinted(Boolean printed) {
        this.printed = printed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}