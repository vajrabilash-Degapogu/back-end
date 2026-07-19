package com.qr_vehicle.QRvehicle.dto;

import lombok.Data;

@Data
public class AssignTagRequest {

    private Long orderId;

    private String tagId;

}