package com.qr_vehicle.QRvehicle.entity;
import jakarta.validation.constraints.NotBlank;
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

    @Column(nullable = false)
    private String tagId;

    @Column(nullable = false)
    private String sheetCode;

    @Column(nullable = false)
    private Integer sheetNumber;

    
    private String emergencyName;

    
    private String emergencyPhone;

    @NotBlank
    private String vehicleNumber;

    @NotBlank
    private String address;

    @Column(unique = true)
    private String uniqueCode;

    public String getTagId() {
    return tagId;
}

public void setTagId(String tagId) {
    this.tagId = tagId;
}

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
}