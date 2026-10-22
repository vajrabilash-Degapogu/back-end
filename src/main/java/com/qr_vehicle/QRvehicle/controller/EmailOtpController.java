package com.qr_vehicle.QRvehicle.controller;

import com.qr_vehicle.QRvehicle.service.EmailOtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/email-otp/order")
@CrossOrigin(origins = {"http://localhost:3000", "https://owntag.in", "https://www.owntag.in"}, allowCredentials = "true")
public class EmailOtpController {
    private final EmailOtpService emailOtpService;

    public EmailOtpController(EmailOtpService emailOtpService) {
        this.emailOtpService = emailOtpService;
    }

    public record EmailRequest(@NotBlank @Email String email) {}
    public record VerifyRequest(@NotBlank @Email String email,
                                @NotBlank @Pattern(regexp = "^[0-9]{6}$") String otp) {}

    @PostMapping("/send")
    public Map<String, String> send(@Valid @RequestBody EmailRequest request) {
        emailOtpService.sendOtp(request.email(), "ORDER");
        return Map.of("message", "Verification code sent. Check your email.");
    }

    @PostMapping("/verify")
    public Map<String, Object> verify(@Valid @RequestBody VerifyRequest request) {
        emailOtpService.verifyOtp(request.email(), "ORDER", request.otp());
        return Map.of("verified", true, "message", "Email verified successfully.");
    }
}
