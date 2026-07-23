package com.qr_vehicle.QRvehicle.config;

import com.qr_vehicle.QRvehicle.entity.Admin;
import com.qr_vehicle.QRvehicle.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (adminRepository.findByUsername(adminUsername).isEmpty()) {

                Admin admin = new Admin();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));

                adminRepository.save(admin);

                System.out.println("✅ Default admin account created.");
            } else {
                System.out.println("✅ Admin account already exists.");
            }
        };
    }
}