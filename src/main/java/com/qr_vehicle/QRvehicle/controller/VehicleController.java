package com.qr_vehicle.QRvehicle.controller;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

// import org.hibernate.query.Page;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.qr_vehicle.QRvehicle.dto.PublicVehicleDto;
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
import org.springframework.web.server.ResponseStatusException;

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
    // "http://localhost:5173"
    "https://owntag.in",
    "https://www.owntag.in"
},
allowCredentials = "true"
)
public class VehicleController {

    private static final Pattern WHATSAPP_USERNAME =
            Pattern.compile("^[A-Za-z0-9._]{3,35}$");

    private final VehicleService vehicleService; // ✅ clear name

    private final OrderService orderService;

    VehicleController(VehicleService vehicleService, OrderService orderService) {
        this.vehicleService = vehicleService;
        this.orderService = orderService;
    } // ✅ FIXED
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<PublicVehicleDto> get(@PathVariable String code) {

    VehicleOwner v = vehicleService.getByCode(code);

    PublicVehicleDto dto = new PublicVehicleDto();
    dto.setOwnerName(v.getOwnerName());
    dto.setVehicleNumber(v.getVehicleNumber());
    dto.setEmergencyContactAvailable(
            v.getEmergencyPhone() != null && !v.getEmergencyPhone().isBlank());
    dto.setWhatsappUsername(v.getWhatsappUsername());
    dto.setWhatsappAvailable(isValidWhatsAppContact(
            v.getWhatsappUsername(), v.getWhatsappUsernameLink()));

    return ResponseEntity.ok()
            .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
            .header("Pragma", "no-cache")
            .body(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<VehicleOwner> getAllVehicles() {
        return vehicleService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/vehicle/count")
    public long getVehicleCount() {
    return vehicleService.count();
    }

    

    // CALL
    
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/vehicle/{id}")
    public void deleteVehicle(@PathVariable Long id) {
    vehicleService.delete(id);
    }

    // WHATSAPP
    @GetMapping("/whatsapp/{code}")
    public ResponseEntity<Void> whatsapp(@PathVariable String code) {
        VehicleOwner owner = vehicleService.getByCode(code);
        String username = normalizeUsername(owner.getWhatsappUsername());
        if (username == null) {
            return ResponseEntity.notFound().build();
        }

        String link = validateWhatsAppLink(owner.getWhatsappUsernameLink(), username);
        if (link == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .location(URI.create(link))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/vehicle/paginated")
    public Page<VehicleOwner> getVehiclesPaginated(
        @RequestParam int page,
        @RequestParam int size) {

    return vehicleService.getPaginated(page, size);
    }

    // 🔥 CONVERT ORDER → CUSTOMER
    
    @PreAuthorize("hasRole('ADMIN')")
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
        String username = normalizeUsername(order.getWhatsappUsername());
        String usernameLink = validateWhatsAppLink(order.getWhatsappUsernameLink(), username);
        if ((username == null) != (usernameLink == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain both the WhatsApp username and sharing link, or neither.");
        }
        v.setWhatsappUsername(username);
        v.setWhatsappUsernameLink(usernameLink);

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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/vehicle/{id}")
    public VehicleOwner updateVehicle(@PathVariable Long id, @RequestBody VehicleOwner updated) {

    VehicleOwner v = vehicleService.getById(id);

    v.setOwnerName(updated.getOwnerName());

    v.setPhoneNumber(updated.getPhoneNumber());

    v.setEmergencyName(updated.getEmergencyName());

    v.setEmergencyPhone(updated.getEmergencyPhone());

    v.setVehicleNumber(updated.getVehicleNumber());

    v.setAddress(updated.getAddress());
    
    String username = normalizeUsername(updated.getWhatsappUsername());
    String usernameLink = validateWhatsAppLink(updated.getWhatsappUsernameLink(), username);
    if ((username == null) != (usernameLink == null)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Enter both the WhatsApp username and official sharing link, or leave both blank.");
    }
    v.setWhatsappUsername(username);
    v.setWhatsappUsernameLink(usernameLink);

    return vehicleService.save(v);
    }

    
    private static String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        String value = username.trim().replaceFirst("^@", "");
        if (!WHATSAPP_USERNAME.matcher(value).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid WhatsApp username format.");
        }
        return value;
    }

    private static String validateWhatsAppLink(String rawLink, String username) {
        if (rawLink == null || rawLink.isBlank()) {
            return null;
        }
        try {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException();
            }

            URI uri = URI.create(rawLink.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"wa.me".equalsIgnoreCase(uri.getHost())
                    || uri.getUserInfo() != null
                    || uri.getPort() != -1
                    || uri.getRawQuery() != null
                    || uri.getFragment() != null) {
                throw new IllegalArgumentException();
            }

            String linkPath = uri.getPath();
            if (linkPath == null || !linkPath.startsWith("/")) {
                throw new IllegalArgumentException();
            }
            String linkedUsername = linkPath.substring(1);
            if (linkedUsername.isBlank()
                    || linkedUsername.contains("/")
                    || !linkedUsername.equalsIgnoreCase(username)) {
                throw new IllegalArgumentException();
            }
            return uri.toASCIIString();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paste the official https://wa.me/YourUsername link copied from WhatsApp. It must match the username.");
        }
    }

    private static boolean isValidWhatsAppContact(String username, String link) {
        try {
            String normalizedUsername = normalizeUsername(username);
            return normalizedUsername != null
                    && validateWhatsAppLink(link, normalizedUsername) != null;
        } catch (RuntimeException ex) {
            return false;
        }
    }

}