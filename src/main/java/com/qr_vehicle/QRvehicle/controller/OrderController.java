package com.qr_vehicle.QRvehicle.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.service.OrderService;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(
    origins = {
        "https://owntag.in",
        "https://www.owntag.in"
        // "http://localhost:3000"
    },
    allowCredentials = "true"
)
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // =========================
    // PLACE ORDER
    // =========================
    @PostMapping
    public Order placeOrder(@jakarta.validation.Valid @RequestBody Order order) {
    return service.save(order);
    }

   

    // =========================
    // GET ALL ORDERS
    // =========================
    @GetMapping
    public List<Order> getOrders() {
        return service.getAll();
    }

    // =========================
    // TRACK ORDERS
    // =========================
    @GetMapping("/track/{phone}")
    public List<Order> trackOrders(@PathVariable String phone) {
        return service.findByPhone(phone);
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @PutMapping("/{id}/status")
    public Order updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return service.updateStatus(id, status);
    }

    // =========================
    // UPDATE ORDER
    // =========================
    @PutMapping("/{id}")
    public Order updateOrder(
            @PathVariable Long id,
            @RequestBody Order updated) {

        Order order = service.getById(id);

        order.setName(updated.getName());
        order.setPhone(updated.getPhone());
        order.setAddress(updated.getAddress());
        order.setVehicleNumber(updated.getVehicleNumber());

        return service.save(order);
    }

    // =========================
    // DELETE ORDER
    // =========================
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        service.delete(id);
    }

    // =========================
    // PAGINATION
    // =========================
    @GetMapping("/paginated")
    public Page<Order> getPaginatedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return service.getPaginated(page, size);
    }

    // =========================
    // TOTAL ORDER COUNT
    // =========================
    @GetMapping("/count")
    public long getOrderCount() {
        return service.count();
    }

    
}