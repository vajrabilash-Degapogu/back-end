package com.qr_vehicle.QRvehicle.service;

import java.util.List;
import java.util.Optional;
import java.net.URI;
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

        order.setWhatsappUsername(normalizeWhatsAppUsername(order.getWhatsappUsername()));
        order.setWhatsappUsernameLink(validateWhatsAppLink(order.getWhatsappUsernameLink(), order.getWhatsappUsername()));
        requireWhatsAppFieldsTogether(order.getWhatsappUsername(), order.getWhatsappUsernameLink());

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
        order.setEmergencyName(updated.getEmergencyName() == null ? "" : updated.getEmergencyName().trim());
        order.setEmergencyPhone(updated.getEmergencyPhone() == null ? "" : updated.getEmergencyPhone().trim());
        order.setVehicleNumber(vehicleNumber);
        order.setWhatsappUsername(normalizeWhatsAppUsername(updated.getWhatsappUsername()));
        order.setWhatsappUsernameLink(validateWhatsAppLink(updated.getWhatsappUsernameLink(), order.getWhatsappUsername()));
        requireWhatsAppFieldsTogether(order.getWhatsappUsername(), order.getWhatsappUsernameLink());

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

    private static void requireWhatsAppFieldsTogether(String username, String link) {
        if ((username == null) != (link == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter both the WhatsApp username and official sharing link, or leave both blank.");
        }
    }

    private static String validateWhatsAppLink(String rawLink, String username) {
        if (rawLink == null || rawLink.isBlank()) {
            return null;
        }

        try {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException();
            }

            URI uri = URI.create(rawLink.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"wa.me".equalsIgnoreCase(uri.getHost())
                    || uri.getUserInfo() != null
                    || uri.getPort() != -1
                    || uri.getRawQuery() != null
                    || uri.getFragment() != null) {
                throw new IllegalArgumentException();
            }

            String path = uri.getPath();
            if (path == null || !path.startsWith("/")) {
                throw new IllegalArgumentException();
            }

            String linkedUsername = path.substring(1);
            if (linkedUsername.isBlank()
                    || linkedUsername.contains("/")
                    || !linkedUsername.equalsIgnoreCase(username)) {
                throw new IllegalArgumentException();
            }

            return uri.toASCIIString();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paste the official https://wa.me/YourUsername link copied from WhatsApp. It must match the username.");
        }
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