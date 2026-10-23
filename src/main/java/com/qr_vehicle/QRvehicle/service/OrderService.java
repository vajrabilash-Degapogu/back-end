package com.qr_vehicle.QRvehicle.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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

    private static final Pattern WHATSAPP_USERNAME =
            Pattern.compile("^[A-Za-z0-9._]{3,35}$");

    private final OrderRepository repo;
    private final VehicleRepository vehicleRepo;
    private final EmailOtpService emailOtpService;

    public OrderService(OrderRepository repo, VehicleRepository vehicleRepo, EmailOtpService emailOtpService) {
        this.repo = repo;
        this.vehicleRepo = vehicleRepo;
        this.emailOtpService = emailOtpService;
    }

    // =========================
    // CREATE NEW ORDER
    // =========================
    public Order save(Order order) {

        String vehicleNumber = order.getVehicleNumber()
                .trim()
                .toUpperCase();

        order.setVehicleNumber(vehicleNumber);

        order.setWhatsappUsername(normalizeWhatsAppUsername(order.getWhatsappUsername()));
        order.setEmail(order.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        // Public checkout cannot set the internal reorder marker.
        order.setReorderSourceTagId(null);

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

        // OTP verification is enforced server-side; clients cannot bypass the UI.
        emailOtpService.consumeVerifiedEmail(order.getEmail(), "ORDER");
        return repo.save(order);
    }

    /**
     * Saves a replacement-tag order created by the authenticated customer dashboard.
     * The controller builds this Order from the tag record owned by that customer.
     */
    public Order saveReorder(Order order) {
        if (order.getName() == null || order.getPhone() == null || order.getAddress() == null
                || order.getVehicleNumber() == null || order.getEmail() == null
                || order.getReorderSourceTagId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required reorder details are missing.");
        }
        order.setVehicleNumber(order.getVehicleNumber().trim().toUpperCase());
        order.setEmail(order.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        order.setWhatsappUsername(normalizeWhatsAppUsername(order.getWhatsappUsername()));
        order.setStatus("PENDING");
        order.setTagAssigned(false);
        order.setTagId(null);
        order.setVehicleOwnerId(null);
        order.setVehicleCode(null);

        // Each reorder requires a recent, single-use email OTP too.
        emailOtpService.consumeVerifiedEmail(order.getEmail(), "ORDER");
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
        order.setEmail(updated.getEmail() == null ? "" : updated.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        order.setAddress(updated.getAddress().trim());
        order.setEmergencyName(updated.getEmergencyName() == null ? "" : updated.getEmergencyName().trim());
        order.setEmergencyPhone(updated.getEmergencyPhone() == null ? "" : updated.getEmergencyPhone().trim());
        order.setVehicleNumber(vehicleNumber);
        order.setWhatsappUsername(normalizeWhatsAppUsername(updated.getWhatsappUsername()));

        return repo.save(order);
    }


    private static String normalizeWhatsAppUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        String value = username.trim();
        if (value.startsWith("@")) {
            value = value.substring(1);
        }

        if (!WHATSAPP_USERNAME.matcher(value).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter a valid WhatsApp username (3-35 letters, numbers, dots or underscores).");
        }
        return value;
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