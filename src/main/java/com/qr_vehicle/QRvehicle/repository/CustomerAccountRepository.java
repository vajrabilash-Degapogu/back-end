package com.qr_vehicle.QRvehicle.repository;

import com.qr_vehicle.QRvehicle.entity.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {
    Optional<CustomerAccount> findByUsernameIgnoreCase(String username);
    Optional<CustomerAccount> findByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
