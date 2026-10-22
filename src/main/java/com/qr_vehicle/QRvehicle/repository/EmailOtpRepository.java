package com.qr_vehicle.QRvehicle.repository;

import com.qr_vehicle.QRvehicle.entity.EmailOtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtpVerification, Long> {
    Optional<EmailOtpVerification> findByEmailAndPurpose(String email, String purpose);
}
