package com.qr_vehicle.QRvehicle.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.qr_vehicle.QRvehicle.dto.TagDashboardResponse;
import com.qr_vehicle.QRvehicle.dto.TagSheetResponse;
import com.qr_vehicle.QRvehicle.entity.TagInventory;
import com.qr_vehicle.QRvehicle.entity.TagSheet;
import com.qr_vehicle.QRvehicle.repository.TagInventoryRepository;
import com.qr_vehicle.QRvehicle.service.TagBatchService;
import com.qr_vehicle.QRvehicle.service.TagInventoryService;
import com.qr_vehicle.QRvehicle.service.TagSheetService;

@RestController
@RequestMapping("/api/tag-sheet")
@CrossOrigin(origins = {
    // "http://localhost:3000",
    // "http://localhost:5173",
    "https://owntag.in",
    "https://www.owntag.in"
})
public class TagSheetController {

    private final TagSheetService tagSheetService;
    private final TagBatchService tagBatchService;
    private final TagInventoryRepository tagInventoryRepository;
    private final TagInventoryService tagInventoryService;

    public TagSheetController(
            TagSheetService tagSheetService,
            TagInventoryRepository tagInventoryRepository,TagBatchService tagBatchService, TagInventoryService tagInventoryService) {

        this.tagSheetService = tagSheetService;
        this.tagInventoryRepository = tagInventoryRepository;
        this.tagBatchService = tagBatchService;
        this.tagInventoryService = tagInventoryService;
    }

    @GetMapping
public List<TagSheetResponse> getAll() {
    return tagSheetService.getAll();
}
    @GetMapping("/dashboard")
public TagDashboardResponse dashboard() {

    TagDashboardResponse dto = new TagDashboardResponse();

    dto.setTotalSheets(
            tagSheetService.totalSheets());

    dto.setTotalTags(
            tagInventoryService.totalTags());

    dto.setAvailableTags(
            tagInventoryService.availableTags());

    dto.setAssignedTags(
            tagInventoryService.assignedTags());

    return dto;
}

    @GetMapping("/{sheetCode}/pdf")
public ResponseEntity<byte[]> downloadSheetPdf(
        @PathVariable String sheetCode) throws Exception {

    byte[] pdf = tagBatchService.generateSheetPdf(sheetCode);

    return ResponseEntity.ok()
            .header(
                    "Content-Disposition",
                    "attachment; filename=" + sheetCode + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}

    @GetMapping("/batch/{batchCode}")
    public List<TagSheet> getBatch(@PathVariable String batchCode) {
        return tagSheetService.getByBatch(batchCode);
    }

    @GetMapping("/{sheetCode}")
    public TagSheet getSheet(@PathVariable String sheetCode) {
        return tagSheetService.getBySheetCode(sheetCode);
    }

    @GetMapping("/{sheetCode}/tags")
    public List<TagInventory> getTags(@PathVariable String sheetCode) {
        return tagInventoryRepository.findBySheetCode(sheetCode);
    }

}