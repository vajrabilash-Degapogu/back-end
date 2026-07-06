package com.qr_vehicle.QRvehicle.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.repository.OrderRepository;
import com.qr_vehicle.QRvehicle.repository.VehicleRepository;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final VehicleRepository vehicleRepo;

    public OrderService(OrderRepository repo, VehicleRepository vehicleRepo) {
        this.repo = repo;
        this.vehicleRepo = vehicleRepo;
    }

    // =========================
    // CREATE NEW ORDER
    // =========================
    public Order save(Order order) {

        String vehicleNumber = order.getVehicleNumber()
                .trim()
                .toUpperCase();

        order.setVehicleNumber(vehicleNumber);

        // Already registered as customer
        if (vehicleRepo.existsByVehicleNumber(vehicleNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vehicle already registered."
            );
        }

        // Already ordered
        if (repo.existsByVehicleNumber(vehicleNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vehicle already ordered."
            );
        }

        return repo.save(order);
    }

    // =========================
    // UPDATE STATUS
    // =========================
    public Order updateStatus(Long id, String status) {

        Order order = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found."
                        ));

        order.setStatus(status);

        return repo.save(order);
    }

    // =========================
    // UPDATE ORDER
    // =========================
    public Order update(Order updated) {

        Order order = repo.findById(updated.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found."
                        ));

        String vehicleNumber = updated.getVehicleNumber()
                .trim()
                .toUpperCase();

        // Already registered as customer (ignore same vehicle if needed)
        if (vehicleRepo.existsByVehicleNumber(vehicleNumber)
                && !vehicleNumber.equals(order.getVehicleNumber())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vehicle already registered."
            );
        }

        // Ignore current order while checking duplicates
        Optional<Order> existing = repo.findByVehicleNumber(vehicleNumber);

        if (existing.isPresent()
                && !existing.get().getId().equals(order.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vehicle already ordered."
            );
        }

        order.setName(updated.getName().trim());
        order.setPhone(updated.getPhone().trim());
        order.setAddress(updated.getAddress().trim());
        order.setEmergencyName(updated.getEmergencyName().trim());
        order.setEmergencyPhone(updated.getEmergencyPhone().trim());
        order.setVehicleNumber(vehicleNumber);

        return repo.save(order);
    }

    // =========================
    // GET ALL
    // =========================
    public List<Order> getAll() {
        return repo.findAll();
    }

    // =========================
    // COUNT
    // =========================
    public long count() {
        return repo.count();
    }

    // =========================
    // PAGINATION
    // =========================
    public Page<Order> getPaginated(int page, int size) {

        if (size > 20) {
            size = 20;
        }

        return repo.findAll(PageRequest.of(page, size));
    }

    // =========================
    // TRACK BY PHONE
    // =========================
    public List<Order> findByPhone(String phone) {
        return repo.findByPhone(phone);
    }

    // =========================
    // DELETE
    // =========================
    public void delete(Long id) {

        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found."
            );
        }

        repo.deleteById(id);
    }

    // =========================
    // GET BY ID
    // =========================
    public Order getById(Long id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found."
                        ));
    }

    public Page<Order> getPaginatedPage(int page, int size) {
    return repo.findByStatusNot(
        "COMPLETED",
        PageRequest.of(page, size)
    );
    }
}