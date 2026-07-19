package com.qr_vehicle.QRvehicle.controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.qr_vehicle.QRvehicle.dto.GenerateBatchRequest;
import com.qr_vehicle.QRvehicle.entity.TagBatch;
import com.qr_vehicle.QRvehicle.service.TagBatchService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tag-batch")
@CrossOrigin(origins = {
    // "http://localhost:3000",
    // "http://localhost:5173",
    "https://owntag.in",
    "https://www.owntag.in"
})
public class TagBatchController {

    private final TagBatchService tagBatchService;

    public TagBatchController(TagBatchService tagBatchService) {
        this.tagBatchService = tagBatchService;
    }

    @GetMapping
    public List<TagBatch> getAll() {

    return tagBatchService.getAll();

}

    @GetMapping("/sheet/{sheetCode}/pdf")
public ResponseEntity<byte[]> sheetPdf(
        @PathVariable String sheetCode) throws Exception {

    byte[] pdf = tagBatchService.generateSheetPdf(sheetCode);

    return ResponseEntity.ok()
            .header(
                    "Content-Disposition",
                    "attachment; filename=" + sheetCode + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}

    @PostMapping("/generate")
    public TagBatch generateBatch(@RequestBody GenerateBatchRequest request) {

        return tagBatchService.generateBatch(
                request.getStickerType(),
                request.getVehicleCount()
        );

    }

    @GetMapping("/{batchCode}/pdf")
    public ResponseEntity<byte[]> pdf(
        @PathVariable String batchCode) throws Exception {

    byte[] pdf =
            tagBatchService.generateBatchPdf(batchCode);

    return ResponseEntity.ok()
            .header(
    "Content-Disposition",
    "attachment; filename=" + batchCode + ".pdf"
)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);

    }

}