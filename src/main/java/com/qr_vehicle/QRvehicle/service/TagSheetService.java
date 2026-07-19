package com.qr_vehicle.QRvehicle.service;

import java.util.List;
import com.qr_vehicle.QRvehicle.repository.TagInventoryRepository;
import com.qr_vehicle.QRvehicle.dto.TagSheetResponse;
import org.springframework.stereotype.Service;

import com.qr_vehicle.QRvehicle.entity.TagSheet;
import com.qr_vehicle.QRvehicle.repository.TagSheetRepository;

@Service
public class TagSheetService {

    private final TagSheetRepository repository;
    
    private final TagInventoryRepository tagInventoryRepository;

    public TagSheetService(
        TagSheetRepository repository,
        TagInventoryRepository tagInventoryRepository) {

    this.repository = repository;
    this.tagInventoryRepository = tagInventoryRepository;
}

    public TagSheet save(TagSheet sheet) {
        return repository.save(sheet);
    }

    public List<TagSheetResponse> getAll() {

    List<TagSheet> sheets = repository.findAll();

    return sheets.stream().map(sheet -> {

        TagSheetResponse dto = new TagSheetResponse();

        dto.setId(sheet.getId());
        dto.setSheetCode(sheet.getSheetCode());
        dto.setBatchCode(sheet.getBatchCode());
        dto.setSheetNumber(sheet.getSheetNumber());
        dto.setVehiclesCount(sheet.getVehiclesCount());
        dto.setStickersCount(sheet.getStickersCount());

        dto.setAvailableTags(
                tagInventoryRepository.countBySheetCodeAndAssignedFalse(
                        sheet.getSheetCode()));

        dto.setAssignedTags(
                tagInventoryRepository.countBySheetCodeAndAssignedTrue(
                        sheet.getSheetCode()));

        return dto;

    }).toList();
}

    public List<TagSheet> getByBatch(String batchCode) {
        return repository.findByBatchCode(batchCode);
    }

    public long totalSheets() {
    return repository.count();
    }

    public TagSheet getBySheetCode(String sheetCode) {
        return repository.findBySheetCode(sheetCode).orElse(null);
    }
    
}