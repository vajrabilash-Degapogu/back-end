package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String address;
    private String status = "PENDING";

    private String vehicleCode;

    private String vehicleNumber; // ✅ user input
}