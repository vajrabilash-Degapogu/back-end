package com.qr_vehicle.QRvehicle.service;

import java.util.List;
import org.springframework.data.domain.*;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repo;

    OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public Order save(Order order) {
        return repo.save(order);
    }

    public List<Order> getAll() {
        return repo.findAll();
    }

    public long count() {
    return repo.count();
    }

    

    public Page<Order> getPaginated(int page, int size) {
    return repo.findAll(PageRequest.of(page, size));
}

    public void delete(Long id) {
    repo.deleteById(id);
    }

    public Order getById(Long id) {
    return repo.findById(id).orElseThrow();
}
}