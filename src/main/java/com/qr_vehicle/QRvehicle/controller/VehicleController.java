package com.qr_vehicle.QRvehicle.controller;

import java.net.URI;
import java.util.List;

// import org.hibernate.query.Page;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api")
@CrossOrigin
public class VehicleController {

    private final VehicleService vehicleService; // ✅ clear name

    private final OrderService orderService;

    VehicleController(VehicleService vehicleService, OrderService orderService) {
        this.vehicleService = vehicleService;
        this.orderService = orderService;
    } // ✅ FIXED

    @PostMapping("/add")
    public VehicleOwner add(@RequestBody VehicleOwner v) {
        return vehicleService.save(v);
    }

    @GetMapping("/vehicle/{code}")
    public VehicleOwner get(@PathVariable String code) {
        return vehicleService.getByCode(code);
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
        URI uri = URI.create("tel:" + v.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    @DeleteMapping("/vehicle/{id}")
    public void deleteVehicle(@PathVariable Long id) {
    vehicleService.delete(id);
    }

    // WHATSAPP
    @GetMapping("/whatsapp/{code}")
    public ResponseEntity<Void> whatsapp(@PathVariable String code) {
        VehicleOwner v = vehicleService.getByCode(code);
        URI uri = URI.create("https://wa.me/" + v.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
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

        Order order = orderService.getById(orderId); // ✅ now works

        VehicleOwner v = new VehicleOwner();
        v.setOwnerName(order.getName());
        v.setPhoneNumber(order.getPhone());
        v.setVehicleNumber(order.getVehicleNumber());

        return vehicleService.save(v); // ✅ correct service
    }

    // QR
    @GetMapping("/qr/{code}")
    public ResponseEntity<byte[]> getQR(@PathVariable String code) throws Exception {

        String url = "https://qrvehicle-frontend.vercel.app/v/" + code;
        byte[] qr = QRGenerator.generateQR(url);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.png")
                .body(qr);
    }

    @PutMapping("/vehicle/{id}")
    public VehicleOwner updateVehicle(@PathVariable Long id, @RequestBody VehicleOwner updated) {

    VehicleOwner v = vehicleService.getById(id);

    v.setOwnerName(updated.getOwnerName());
    v.setPhoneNumber(updated.getPhoneNumber());
    v.setVehicleNumber(updated.getVehicleNumber());

    return vehicleService.save(v);
    }
}