package com.qr_vehicle.QRvehicle.service;

import java.util.List;
import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import com.qr_vehicle.QRvehicle.entity.VehicleOwner;
import com.qr_vehicle.QRvehicle.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository repo;

    VehicleService(VehicleRepository repo) {
        this.repo = repo;
    }

    public VehicleOwner save(VehicleOwner v) {
        v.setUniqueCode(UUID.randomUUID().toString());
        return repo.save(v);
    }

    public VehicleOwner getByCode(String code) {
        return repo.findByUniqueCode(code).orElseThrow();
    }

    public void delete(Long id) {
    repo.deleteById(id);
    }

    public long count() {
    return repo.count();
    }

    

    public Page<VehicleOwner> getPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    return repo.findAll(pageable);
    }

    public VehicleOwner getById(Long id) {
    return repo.findById(id).orElseThrow();
    }

    public List<VehicleOwner> getAll() {
    return repo.findAll();
    }
}