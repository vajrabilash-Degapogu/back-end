package com.qr_vehicle.QRvehicle.controller;

import com.qr_vehicle.QRvehicle.entity.CustomerAccount;
import com.qr_vehicle.QRvehicle.repository.AdminRepository;
import com.qr_vehicle.QRvehicle.repository.CustomerAccountRepository;
import com.qr_vehicle.QRvehicle.security.JwtService;
import com.qr_vehicle.QRvehicle.service.EmailOtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer-auth")
@CrossOrigin(origins = {"http://localhost:3000", "https://owntag.in", "https://www.owntag.in"}, allowCredentials = "true")
public class CustomerAuthController {
    private static final String REGISTER = "CUSTOMER_REGISTER";
    private static final String USERNAME_RECOVERY = "CUSTOMER_USERNAME_RECOVERY";
    private static final String PASSWORD_RESET = "CUSTOMER_PASSWORD_RESET";

    private final CustomerAccountRepository accounts;
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;
    private final EmailOtpService emailOtpService;
    private final JwtService jwtService;

    public CustomerAuthController(CustomerAccountRepository accounts, AdminRepository admins,
                                  PasswordEncoder passwordEncoder, EmailOtpService emailOtpService, JwtService jwtService) {
        this.accounts = accounts;
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
        this.emailOtpService = emailOtpService;
        this.jwtService = jwtService;
    }

    public record EmailRequest(@NotBlank @Email String email) {}
    public record VerifyRequest(@NotBlank @Email String email,
                                @NotBlank @Pattern(regexp = "^[0-9]{6}$") String otp) {}
    public record RegisterRequest(
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9._-]{3,30}$", message = "Username must be 3-30 letters, numbers, dots, underscores or hyphens") String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72, message = "Password must be 8-72 characters") String password
    ) {}
    public record LoginRequest(@NotBlank String identifier, @NotBlank String password) {}
    public record ResetPasswordRequest(@NotBlank @Email String email,
                                       @NotBlank @Pattern(regexp = "^[0-9]{6}$") String otp,
                                       @NotBlank @Size(min = 8, max = 72) String newPassword) {}

    @PostMapping("/register/send-otp")
    public Map<String, String> sendRegistrationOtp(@Valid @RequestBody EmailRequest request) {
        emailOtpService.sendOtp(request.email(), REGISTER);
        return Map.of("message", "Verification code sent. Check your email.");
    }

    @PostMapping("/register/verify-otp")
    public Map<String, Object> verifyRegistrationOtp(@Valid @RequestBody VerifyRequest request) {
        emailOtpService.verifyOtp(request.email(), REGISTER, request.otp());
        return Map.of("verified", true, "message", "Email verified.");
    }

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegisterRequest request) {
        String username = request.username().trim();
        String email = EmailOtpService.normalizeEmail(request.email());
        if (accounts.existsByUsernameIgnoreCase(username) || admins.findByUsernameIgnoreCase(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is already taken.");
        }
        if (accounts.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
        }
        emailOtpService.consumeVerifiedEmail(email, REGISTER);

        CustomerAccount account = new CustomerAccount();
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(request.password()));
        CustomerAccount saved = accounts.save(account);
        return loginResponse(saved);
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        String identifier = request.identifier().trim();
        Optional<CustomerAccount> account = accounts.findByUsernameIgnoreCase(identifier);
        if (account.isEmpty()) account = accounts.findByEmailIgnoreCase(identifier);
        CustomerAccount found = account.orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password."));
        if (!passwordEncoder.matches(request.password(), found.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password.");
        }
        return loginResponse(found);
    }

    @PostMapping("/forgot-username/send-otp")
    public Map<String, String> sendUsernameRecoveryOtp(@Valid @RequestBody EmailRequest request) {
        emailOtpService.sendOtp(request.email(), USERNAME_RECOVERY);
        return Map.of("message", "If you continue, a verification code will be sent to this email.");
    }

    @PostMapping("/forgot-username/verify-otp")
    public Map<String, String> recoverUsername(@Valid @RequestBody VerifyRequest request) {
        String email = EmailOtpService.normalizeEmail(request.email());
        emailOtpService.verifyOtp(email, USERNAME_RECOVERY, request.otp());
        emailOtpService.consumeVerifiedEmail(email, USERNAME_RECOVERY);
        accounts.findByEmailIgnoreCase(email).ifPresent(account ->
            emailOtpService.sendMessage(email, "Your OwnTag username",
                "Your OwnTag username is: " + account.getUsername() + "\n\nIf you did not request this email, ignore this message."));
        return Map.of("message", "If an account exists for this email, its username has been sent to that address.");
    }

    @PostMapping("/forgot-password/send-otp")
    public Map<String, String> sendPasswordResetOtp(@Valid @RequestBody EmailRequest request) {
        emailOtpService.sendOtp(request.email(), PASSWORD_RESET);
        return Map.of("message", "If you continue, a verification code will be sent to this email.");
    }

    @PostMapping("/forgot-password/reset")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String email = EmailOtpService.normalizeEmail(request.email());
        emailOtpService.verifyOtp(email, PASSWORD_RESET, request.otp());
        CustomerAccount account = accounts.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "The email or OTP is invalid."));
        emailOtpService.consumeVerifiedEmail(email, PASSWORD_RESET);
        account.setPassword(passwordEncoder.encode(request.newPassword()));
        accounts.save(account);
        return Map.of("message", "Password reset successfully. You can now log in.");
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Map<String, String> me(Authentication authentication) {
        CustomerAccount account = accounts.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer account not found."));
        return Map.of("username", account.getUsername(), "email", account.getEmail());
    }

    private Map<String, String> loginResponse(CustomerAccount account) {
        return Map.of("token", jwtService.generateToken(account.getUsername()),
            "username", account.getUsername(), "email", account.getEmail());
    }
}
