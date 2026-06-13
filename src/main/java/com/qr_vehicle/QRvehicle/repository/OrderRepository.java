package com.qr_vehicle.QRvehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.qr_vehicle.QRvehicle.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}