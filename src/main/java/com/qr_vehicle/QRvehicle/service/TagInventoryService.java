package com.qr_vehicle.QRvehicle.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import com.qr_vehicle.QRvehicle.dto.AssignTagRequest;
import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.entity.VehicleOwner;
import com.qr_vehicle.QRvehicle.repository.OrderRepository;
import com.qr_vehicle.QRvehicle.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import com.qr_vehicle.QRvehicle.entity.TagInventory;
import com.qr_vehicle.QRvehicle.repository.TagInventoryRepository;

@Service
public class TagInventoryService {

    private final TagInventoryRepository repository;
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;

    private final SecureRandom random = new SecureRandom();

    public TagInventoryService(
        TagInventoryRepository repository,
        OrderRepository orderRepository,
        VehicleRepository vehicleRepository) {

    this.repository = repository;
    this.orderRepository = orderRepository;
    this.vehicleRepository = vehicleRepository;
    }

    public TagInventory save(TagInventory inventory) {
        return repository.save(inventory);
    }

    public List<TagInventory> getAll() {
        return repository.findAll();
    }

    public List<TagInventory> getAvailableTags() {
        return repository.findByAssignedFalse();
    }

    public TagInventory getByTagId(String tagId) {
        return repository.findByTagId(tagId).orElse(null);
    }

    public String generateRandomTagId() {

    while (true) {

        String tag = String.valueOf(
                10000000 + random.nextInt(90000000));

        

        if (repository.findByTagId(tag).isEmpty()) {

           

            return tag;
        }

        
    }
}

    public long totalTags() {
    return repository.count();
    }

    public long availableTags() {
        return repository.countByAssigned(false);
    }

    public long assignedTags() {
        return repository.countByAssigned(true);
    }

    public String generateUniqueCode() {

    String uuid = UUID.randomUUID().toString();

    

    return uuid;
}

    
    @Transactional
    public VehicleOwner assignTag(AssignTagRequest request) {

    Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found"));

    TagInventory tag = repository.findByTagId(request.getTagId())
            .orElseThrow(() -> new RuntimeException("Tag not found"));

    if (Boolean.TRUE.equals(tag.getAssigned())) {
        throw new RuntimeException("Tag already assigned.");
    }

    if (vehicleRepository.existsByVehicleNumber(order.getVehicleNumber())) {
        throw new RuntimeException("Vehicle already exists.");
    }

    VehicleOwner vehicle = new VehicleOwner();

    vehicle.setOwnerName(order.getName());
    vehicle.setPhoneNumber(order.getPhone());
    vehicle.setEmergencyName(order.getEmergencyName());
    vehicle.setEmergencyPhone(order.getEmergencyPhone());
    vehicle.setVehicleNumber(order.getVehicleNumber());
    vehicle.setAddress(order.getAddress());

    // This is the QR UUID already printed on the sticker
    vehicle.setUniqueCode(tag.getUniqueCode());
    vehicle.setTagId(tag.getTagId());
    vehicle.setSheetCode(tag.getSheetCode());
    vehicle.setSheetNumber(tag.getSheetNumber());

    vehicle = vehicleRepository.save(vehicle);

    tag.setAssigned(true);
    tag.setOrderId(order.getId());
    tag.setVehicleId(vehicle.getId());
    tag.setAssignedAt(LocalDateTime.now());

    repository.save(tag);

    order.setTagAssigned(true);
    order.setTagId(tag.getTagId());
    order.setVehicleOwnerId(vehicle.getId());
    order.setVehicleCode(tag.getUniqueCode());
    order.setStatus("COMPLETED");

    orderRepository.save(order);

    return vehicle;
    }

}