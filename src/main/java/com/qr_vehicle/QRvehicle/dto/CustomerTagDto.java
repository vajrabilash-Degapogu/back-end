package com.qr_vehicle.QRvehicle.dto;

public record CustomerTagDto(
    String tagId,
    String vehicleNumber,
    String uniqueCode,
    String sheetCode,
    Integer sheetNumber,
    String stickerType
) {}
