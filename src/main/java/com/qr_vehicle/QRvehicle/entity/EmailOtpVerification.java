package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "email_otp_verifications",
       uniqueConstraints = @UniqueConstraint(name = "uk_email_otp_email_purpose", columnNames = {"email", "purpose"}))
@Getter
@Setter
public class EmailOtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 40)
    private String purpose;

    @Column(length = 64)
    private String otpHash;

    private Instant expiresAt;
    private Instant verifiedUntil;
    private Instant lastSentAt;

    @Column(nullable = false)
    private int attempts;
}
