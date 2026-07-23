package com.qr_vehicle.QRvehicle.controller;

import java.net.URI;
import java.util.List;

// import org.hibernate.query.Page;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.entity.VehicleOwner;
import com.qr_vehicle.QRvehicle.service.OrderService;
import com.qr_vehicle.QRvehicle.service.VehicleService;
import com.qr_vehicle.QRvehicle.util.QRGenerator;
import com.qr_vehicle.QRvehicle.util.TagPdfGenerator;

@RestController
@RequestMapping("/api")
@CrossOrigin(
origins = {
    // "http://localhost:3000",
    // "http://localhost:5173",
    "https://owntag.in",
    "https://www.owntag.in"
},
allowCredentials = "true"
)
public class VehicleController {

    private final VehicleService vehicleService; // ✅ clear name

    private final OrderService orderService;

    VehicleController(VehicleService vehicleService, OrderService orderService) {
        this.vehicleService = vehicleService;
        this.orderService = orderService;
    } // ✅ FIXED

    @PostMapping("/add")
    public VehicleOwner add( @Validated @RequestBody VehicleOwner v) {
        return vehicleService.save(v);
    }
    
    @GetMapping("/tag-pdf/{code}")
    public ResponseEntity<byte[]> getTagPdf(@PathVariable String code) throws Exception {

    String url = "https://owntag.in/v/" + code;
    // String url = "http://localhost:3000/v/" + code;
    byte[] qr = QRGenerator.generateQR(url);

    byte[] pdf = TagPdfGenerator.generateTag(qr);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
        .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
        .body(pdf);
    }
    
    @GetMapping("/vehicle/{code}")
    public ResponseEntity<VehicleOwner> get(@PathVariable String code) {

    VehicleOwner v = vehicleService.getByCode(code);

    return ResponseEntity.ok()
            .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
            .header("Pragma", "no-cache")
            .body(v);
    }
    
    @GetMapping("/all")
    public List<VehicleOwner> getAllVehicles() {
        return vehicleService.getAll();
    }

    
    @GetMapping("/vehicle/count")
    public long getVehicleCount() {
    return vehicleService.count();
    }

    

    // CALL
    
    @GetMapping("/call/{code}")
public ResponseEntity<Void> call(@PathVariable String code) {

    VehicleOwner v = vehicleService.getByCode(code);

    String phone = v.getPhoneNumber();

    // Remove spaces, +, -, etc.
    phone = phone.replaceAll("\\D", "");

    // Add India country code if missing
    if (!phone.startsWith("91")) {
        phone = "91" + phone;
    }

    URI uri = URI.create("tel:+" + phone);

    return ResponseEntity.status(HttpStatus.FOUND)
            .location(uri)
            .build();
}

    @DeleteMapping("/vehicle/{id}")
    public void deleteVehicle(@PathVariable Long id) {
    vehicleService.delete(id);
    }

    // WHATSAPP
    @GetMapping("/whatsapp/{code}")
    public ResponseEntity<Void> whatsapp(@PathVariable String code) {

    VehicleOwner v = vehicleService.getByCode(code);

    String phone = v.getPhoneNumber();

    // Remove spaces, +, -, etc.
    phone = phone.replaceAll("\\D", "");

    // Add India country code if missing
    if (!phone.startsWith("91")) {
        phone = "+91" + phone;
    }

    URI uri = URI.create("https://wa.me/" + phone);

    return ResponseEntity.status(HttpStatus.FOUND)
            .location(uri)
            .build();
    }

    
    @GetMapping("/vehicle/paginated")
    public Page<VehicleOwner> getVehiclesPaginated(
        @RequestParam int page,
        @RequestParam int size) {

    return vehicleService.getPaginated(page, size);
    }

    // 🔥 CONVERT ORDER → CUSTOMER
    
    @PostMapping("/from-order/{orderId}")
    public VehicleOwner createFromOrder(@PathVariable Long orderId) {

    Order order = orderService.getById(orderId);

    VehicleOwner v = new VehicleOwner();

        v.setOwnerName(order.getName());

        v.setPhoneNumber(order.getPhone());

        v.setEmergencyName(order.getEmergencyName());

        v.setEmergencyPhone(order.getEmergencyPhone());

        v.setVehicleNumber(order.getVehicleNumber());

        v.setAddress(order.getAddress());

    VehicleOwner saved = vehicleService.save(v);

    // ✅ DELETE ORDER AFTER SUCCESS
    orderService.delete(orderId);

    return saved;
    }

    // QR
    @GetMapping("/qr/{code}")
    public ResponseEntity<byte[]> getQR(@PathVariable String code) throws Exception {

        String url = "https://owntag.in/v/" + code;
        // String url = "http://localhost:3000/v/" + code;
        byte[] qr = QRGenerator.generateQR(url);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.png")
                .body(qr);
    }

   @GetMapping("/call-emergency/{code}")
public ResponseEntity<Void> callEmergency(@PathVariable String code) {

    VehicleOwner v = vehicleService.getByCode(code);

    String phone = v.getEmergencyPhone();

    phone = phone.replaceAll("\\D", "");

    if (!phone.startsWith("91")) {
        phone = "91" + phone;
    }

    URI uri = URI.create("tel:+" + phone);

    return ResponseEntity.status(HttpStatus.FOUND)
            .location(uri)
            .build();
}

    @PutMapping("/vehicle/{id}")
    public VehicleOwner updateVehicle(@PathVariable Long id, @RequestBody VehicleOwner updated) {

    VehicleOwner v = vehicleService.getById(id);

    v.setOwnerName(updated.getOwnerName());

    v.setPhoneNumber(updated.getPhoneNumber());

    v.setEmergencyName(updated.getEmergencyName());

    v.setEmergencyPhone(updated.getEmergencyPhone());

    v.setVehicleNumber(updated.getVehicleNumber());

    v.setAddress(updated.getAddress());

    return vehicleService.save(v);
    }
}