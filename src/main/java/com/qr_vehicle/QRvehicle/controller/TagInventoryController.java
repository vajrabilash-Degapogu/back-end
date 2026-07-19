package com.qr_vehicle.QRvehicle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.qr_vehicle.QRvehicle.dto.AssignTagRequest;
import com.qr_vehicle.QRvehicle.entity.TagInventory;
import com.qr_vehicle.QRvehicle.entity.VehicleOwner;
import com.qr_vehicle.QRvehicle.service.TagInventoryService;

@RestController
@RequestMapping("/api/tag-inventory")
@CrossOrigin(origins = {
    // "http://localhost:3000",
    // "http://localhost:5173",
    "https://owntag.in",
    "https://www.owntag.in"
})
public class TagInventoryController {

    private final TagInventoryService service;

    public TagInventoryController(TagInventoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<TagInventory> getAll() {
        return service.getAll();
    }

    @GetMapping("/available")
    public List<TagInventory> available() {
        return service.getAvailableTags();
    }

    @GetMapping("/{tagId}")
    public TagInventory getTag(@PathVariable String tagId) {
        return service.getByTagId(tagId);
    }

    @PutMapping("/assign")
    public VehicleOwner assignTag(@RequestBody AssignTagRequest request) {
        return service.assignTag(request);
    }
}