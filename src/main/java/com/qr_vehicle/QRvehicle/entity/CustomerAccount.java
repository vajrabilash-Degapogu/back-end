package com.qr_vehicle.QRvehicle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_accounts",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_customer_account_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_customer_account_email", columnNames = "email")
       })
@Getter
@Setter
public class CustomerAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    @Column(nullable = false, length = 254, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;
}
