package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "Email address is required")
    @Email(message = "Enter a valid email address")
    @Column(length = 254)
    private String email;

    private String emergencyName;


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

    private String tagId;

    @Column(name = "sticker_type")
    private String stickerType;

    private Boolean tagAssigned = false;

    private Long vehicleOwnerId;

    // Set only by the authenticated customer reorder flow; points to the tag being reordered.
    @Column(length = 8)
    private String reorderSourceTagId;

    // Optional WhatsApp username-based contact for the QR tag.
    @Column(length = 35)
    private String whatsappUsername;
}