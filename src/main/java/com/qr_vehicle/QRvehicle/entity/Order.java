package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter a valid 10-digit Indian mobile number"
    )
    private String phone;

    @NotBlank(message = "Emergency contact name is required")
    @Size(min = 3, max = 100)
    private String emergencyName;

    @NotBlank(message = "Emergency contact number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter valid emergency contact number"
    )
    private String emergencyPhone;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 250, message = "Address must be between 10 and 250 characters")
    private String address;

    private String status = "PENDING";

    private String vehicleCode;

    @NotBlank(message = "Vehicle number is required")
    @Pattern(
        regexp = "^[A-Za-z]{2}[0-9]{2}[A-Za-z]{1,2}[0-9]{4}$",
        message = "Enter a valid vehicle number (e.g. AP39AB1234)"
    )
    private String vehicleNumber;
}