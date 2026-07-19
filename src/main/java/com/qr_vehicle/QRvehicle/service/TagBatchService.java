package com.qr_vehicle.QRvehicle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qr_vehicle.QRvehicle.entity.TagBatch;
import com.qr_vehicle.QRvehicle.entity.TagInventory;
import com.qr_vehicle.QRvehicle.entity.TagSheet;
import com.qr_vehicle.QRvehicle.repository.TagBatchRepository;
import com.qr_vehicle.QRvehicle.repository.TagInventoryRepository;
import com.qr_vehicle.QRvehicle.util.BatchCodeGenerator;
import com.qr_vehicle.QRvehicle.util.StickerSheetGenerator;

import jakarta.transaction.Transactional;

@Service
public class TagBatchService {

    private final TagBatchRepository batchRepository;
    private final TagInventoryService inventoryService;
    private final TagInventoryRepository tagInventoryRepository;
    private final TagSheetService tagSheetService;

    public TagBatchService(
        TagBatchRepository batchRepository,
        TagInventoryRepository tagInventoryRepository,
        TagInventoryService inventoryService,
        TagSheetService tagSheetService) {

    this.batchRepository = batchRepository;
    this.tagInventoryRepository = tagInventoryRepository;
    this.inventoryService = inventoryService;
    this.tagSheetService = tagSheetService;
}

    public TagBatch save(TagBatch batch) {
        return batchRepository.save(batch);
    }

    public List<TagBatch> getAll() {
        return batchRepository.findAll();
    }

    public TagBatch getById(Long id) {
        return batchRepository.findById(id).orElse(null);
    }

    public TagBatch getByBatchCode(String batchCode) {
        return batchRepository.findByBatchCode(batchCode).orElse(null);
    }

    public void delete(Long id) {
        batchRepository.deleteById(id);
    }

    public byte[] generateSheetPdf(String sheetCode) throws Exception {

    List<TagInventory> tags =
            tagInventoryRepository.findBySheetCode(sheetCode);

    return StickerSheetGenerator.generate(tags);
}

    public byte[] generateBatchPdf(String batchCode)
        throws Exception {

    List<TagInventory> inventory = tagInventoryRepository.findByBatchCode(batchCode);
                    

    return StickerSheetGenerator.generate(inventory);

    }

    @Transactional
public TagBatch generateBatch(String stickerType, Integer vehicleCount) {

    final int VEHICLES_PER_SHEET = 10;

    long totalBatch = batchRepository.count() + 1;

    String batchCode = BatchCodeGenerator.generate(
            stickerType,
            totalBatch
    );

    TagBatch batch = new TagBatch();

    batch.setBatchCode(batchCode);
    batch.setStickerType(stickerType);
    batch.setVehiclesCount(vehicleCount);
    batch.setStickersCount(vehicleCount * 2);
    batch.setPrinted(false);

    batch = batchRepository.save(batch);

    int totalSheets =
            (int) Math.ceil((double) vehicleCount / VEHICLES_PER_SHEET);

    int remainingVehicles = vehicleCount;

    for (int sheet = 1; sheet <= totalSheets; sheet++) {

        int vehiclesThisSheet =
                Math.min(remainingVehicles, VEHICLES_PER_SHEET);

        String sheetCode =
                batchCode + "-S" + String.format("%02d", sheet);

        TagSheet tagSheet = new TagSheet();

        tagSheet.setBatchCode(batchCode);
        tagSheet.setSheetCode(sheetCode);
        tagSheet.setSheetNumber(sheet);
        tagSheet.setVehiclesCount(vehiclesThisSheet);
        tagSheet.setStickersCount(vehiclesThisSheet * 2);

        tagSheetService.save(tagSheet);

        generateSheetInventory(
                batchCode,
                sheetCode,
                sheet,
                stickerType,
                vehiclesThisSheet
        );

        remainingVehicles -= vehiclesThisSheet;
    }

    return batch;
}
private void generateSheetInventory(
        String batchCode,
        String sheetCode,
        int sheetNumber,
        String stickerType,
        int vehicles) {

    int pair = 1;

    for (int row = 1; row <= 5; row++) {

        for (int col = 1; col <= 4; col += 2) {

            if (pair > vehicles)
                return;

            TagInventory inventory = new TagInventory();

            inventory.setBatchCode(batchCode);
            inventory.setSheetCode(sheetCode);
            inventory.setSheetNumber(sheetNumber);

            inventory.setStickerType(stickerType);

            inventory.setTagId(
                    inventoryService.generateRandomTagId());

            inventory.setUniqueCode(
                    inventoryService.generateUniqueCode());

            inventory.setAssigned(false);

            inventory.setSheetRow(row);
            inventory.setSheetColumn(col);
            inventory.setPairIndex(pair);

            inventoryService.save(inventory);

            pair++;
        }
    }
}

}