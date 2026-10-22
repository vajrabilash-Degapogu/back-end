package com.qr_vehicle.QRvehicle.controller;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.server.ResponseStatusException;

import com.qr_vehicle.QRvehicle.dto.PublicVehicleDto;
import com.qr_vehicle.QRvehicle.entity.Order;
import com.qr_vehicle.QRvehicle.entity.VehicleOwner;
import com.qr_vehicle.QRvehicle.service.OrderService;
import com.qr_vehicle.QRvehicle.service.VehicleService;
import com.qr_vehicle.QRvehicle.util.QRGenerator;
import com.qr_vehicle.QRvehicle.util.TagPdfGenerator;

@RestController
@RequestMapping("/api")
@CrossOrigin(
    origins = {"https://owntag.in", "https://www.owntag.in"},
    allowCredentials = "true"
)
public class VehicleController {

    private static final Pattern WHATSAPP_USERNAME =
            Pattern.compile("^[A-Za-z0-9._]{3,35}$");

    // Redirect only to well-known WhatsApp-owned hosts. Confirm the exact share
    // link pattern from the user's WhatsApp app before enabling production.
    private static final Set<String> WHATSAPP_HOSTS =
            Set.of("wa.me", "whatsapp.com", "www.whatsapp.com", "api.whatsapp.com");

    private final VehicleService vehicleService;
    private final OrderService orderService;

    VehicleController(VehicleService vehicleService, OrderService orderService) {
        this.vehicleService = vehicleService;
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public VehicleOwner add(@Validated @RequestBody VehicleOwner v) {
        return vehicleService.save(v);
    }

    @GetMapping("/tag-pdf/{code}")
    public ResponseEntity<byte[]> getTagPdf(@PathVariable String code) throws Exception {
        String url = "https://owntag.in/v/" + code;
        byte[] qr = QRGenerator.generateQR(url);
        byte[] pdf = TagPdfGenerator.generateTag(qr);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(pdf);
    }

    @GetMapping("/vehicle/{code}")
    public ResponseEntity<PublicVehicleDto> get(@PathVariable String code) {
        VehicleOwner owner = vehicleService.getByCode(code);

        PublicVehicleDto dto = new PublicVehicleDto();
        dto.setOwnerName(owner.getOwnerName());
        dto.setVehicleNumber(owner.getVehicleNumber());
        dto.setEmergencyContactAvailable(owner.getEmergencyPhone() != null && !owner.getEmergencyPhone().isBlank());
        dto.setWhatsappUsername(owner.getWhatsappUsername());
        dto.setWhatsappAvailable(isValidWhatsAppContact(
                owner.getWhatsappUsername(), owner.getWhatsappUsernameLink()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
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

    @GetMapping("/call/{code}")
    public ResponseEntity<Void> call(@PathVariable String code) {
        VehicleOwner owner = vehicleService.getByCode(code);
        String phone = owner.getPhoneNumber().replaceAll("\\D", "");
        if (!phone.startsWith("91")) {
            phone = "91" + phone;
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("tel:+" + phone))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/vehicle/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.delete(id);
    }

    // No phone-number fallback: the stored official link is the only redirect.
    @GetMapping("/whatsapp/{code}")
    public ResponseEntity<Void> whatsapp(@PathVariable String code) {
        VehicleOwner owner = vehicleService.getByCode(code);
        String username = normalizeUsername(owner.getWhatsappUsername());
        String link = validateWhatsAppLink(owner.getWhatsappUsernameLink());

        if (username == null || link == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .location(URI.create(link))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/vehicle/{id}")
    public VehicleOwner updateVehicle(@PathVariable Long id, @RequestBody VehicleOwner updated) {
        VehicleOwner owner = vehicleService.getById(id);
        owner.setOwnerName(updated.getOwnerName());
        owner.setPhoneNumber(updated.getPhoneNumber());
        owner.setEmergencyName(updated.getEmergencyName());
        owner.setEmergencyPhone(updated.getEmergencyPhone());
        owner.setVehicleNumber(updated.getVehicleNumber());
        owner.setAddress(updated.getAddress());
        owner.setWhatsappUsername(normalizeUsername(updated.getWhatsappUsername()));
        owner.setWhatsappUsernameLink(validateWhatsAppLink(updated.getWhatsappUsernameLink()));

        if ((owner.getWhatsappUsername() == null) != (owner.getWhatsappUsernameLink() == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enter both the WhatsApp username and official sharing link, or leave both blank.");
        }

        return vehicleService.save(owner);
    }

    private static String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        String value = username.trim().replaceFirst("^@", "");
        if (!WHATSAPP_USERNAME.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid WhatsApp username format.");
        }
        return value;
    }

    private static String validateWhatsAppLink(String rawLink) {
        if (rawLink == null || rawLink.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(rawLink.trim());
            String host = uri.getHost();
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || host == null
                    || !WHATSAPP_HOSTS.contains(host.toLowerCase(Locale.ROOT))
                    || uri.getUserInfo() != null
                    || uri.getPort() != -1
                    || uri.getFragment() != null) {
                throw new IllegalArgumentException();
            }

            String query = uri.getRawQuery();
            if (query != null) {
                for (String parameter : query.toLowerCase(Locale.ROOT).split("&")) {
                    String key = parameter.split("=", 2)[0];
                    if ("phone".equals(key) || "phone_number".equals(key)) {
                        throw new IllegalArgumentException();
                    }
                }
            }

            String path = uri.getPath();
            if (path != null) {
                String firstSegment = path.replaceFirst("^/", "").split("/", 2)[0];
                String candidate = firstSegment.startsWith("+")
                        ? firstSegment.substring(1) : firstSegment;
                boolean allDigits = !candidate.isEmpty()
                        && candidate.chars().allMatch(Character::isDigit);
                if (allDigits && candidate.length() >= 8 && candidate.length() <= 15) {
                    throw new IllegalArgumentException();
                }
            }

            return uri.toASCIIString();
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(
                    "Use the official WhatsApp username sharing link copied from WhatsApp.");
        }
    }

    private static boolean isValidWhatsAppContact(String username, String link) {
        try {
            return normalizeUsername(username) != null && validateWhatsAppLink(link) != null;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @GetMapping("/qr/{code}")
    public ResponseEntity<byte[]> getQR(@PathVariable String code) throws Exception {
        String url = "https://owntag.in/v/" + code;
        byte[] qr = QRGenerator.generateQR(url);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.png")
                .body(qr);
    }

    @GetMapping("/call-emergency/{code}")
    public ResponseEntity<Void> callEmergency(@PathVariable String code) {
        VehicleOwner owner = vehicleService.getByCode(code);
        String phone = owner.getEmergencyPhone();
        if (phone == null || phone.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        phone = phone.replaceAll("\\D", "");
        if (!phone.startsWith("91")) {
            phone = "91" + phone;
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("tel:+" + phone))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/vehicle/paginated")
    public Page<VehicleOwner> getVehiclesPaginated(
            @RequestParam int page,
            @RequestParam int size) {
        return vehicleService.getPaginated(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/from-order/{orderId}")
    public VehicleOwner createFromOrder(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        VehicleOwner owner = new VehicleOwner();
        owner.setOwnerName(order.getName());
        owner.setPhoneNumber(order.getPhone());
        owner.setEmergencyName(order.getEmergencyName());
        owner.setEmergencyPhone(order.getEmergencyPhone());
        owner.setVehicleNumber(order.getVehicleNumber());
        owner.setAddress(order.getAddress());
        owner.setWhatsappUsername(normalizeUsername(order.getWhatsappUsername()));
        owner.setWhatsappUsernameLink(validateWhatsAppLink(order.getWhatsappUsernameLink()));

        VehicleOwner saved = vehicleService.save(owner);
        orderService.delete(orderId);
        return saved;
    }
}
