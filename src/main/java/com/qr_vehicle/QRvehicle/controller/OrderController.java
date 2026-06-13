package com.qr_vehicle.QRvehicle.controller;

import java.util.List;

import org.springframework.data.domain.*;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.service.OrderService;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService service;

  OrderController(OrderService service) {
    this.service = service;
  }

    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        return service.save(order); // ✅ save directly
    }

    @GetMapping
    public List<Order> getOrders() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
    service.delete(id);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam String status) {

    Order order = service.getById(id);
    order.setStatus(status);

    return service.save(order);
    }

    @GetMapping("/paginated")
public Page<Order> getPaginatedOrders(
        @RequestParam int page,
        @RequestParam int size) {

    return service.getPaginated(page, size);
}

    @GetMapping("/count")
    public long getOrderCount() {
    return service.count();
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order updated) {

    Order o = service.getById(id);

    o.setName(updated.getName());
    o.setPhone(updated.getPhone());
    o.setAddress(updated.getAddress());
    o.setVehicleNumber(updated.getVehicleNumber());

    return service.save(o);
    }
}