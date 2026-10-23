package com.qr_vehicle.QRvehicle.service;

import com.qr_vehicle.QRvehicle.entity.EmailOtpVerification;
import com.qr_vehicle.QRvehicle.repository.EmailOtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Service
public class EmailOtpService {
    private static final Duration OTP_LIFETIME = Duration.ofMinutes(10);
    private static final Duration VERIFIED_LIFETIME = Duration.ofMinutes(10);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 5;

    private final EmailOtpRepository repository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.mail.from:}")
    private String fromAddress;

    public EmailOtpService(EmailOtpRepository repository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void sendOtp(String rawEmail, String purpose) {
        String email = normalizeEmail(rawEmail);
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Email delivery is not configured. Set SMTP_USERNAME, SMTP_PASSWORD, and SMTP_FROM.");
        }

        Instant now = Instant.now();
        EmailOtpVerification record = repository.findByEmailAndPurpose(email, purpose)
            .orElseGet(EmailOtpVerification::new);
        if (record.getLastSentAt() != null && record.getLastSentAt().plus(RESEND_COOLDOWN).isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Please wait 60 seconds before requesting another OTP.");
        }

        String otp = String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
        record.setEmail(email);
        record.setPurpose(purpose);
        record.setOtpHash(passwordEncoder.encode(otp));
        record.setExpiresAt(now.plus(OTP_LIFETIME));
        record.setVerifiedUntil(null);
        record.setLastSentAt(now);
        record.setAttempts(0);
        repository.save(record);

        String purposeText = switch (purpose) {
            case "ORDER" -> "verify your email before placing your OwnTag order";
            case "CUSTOMER_REGISTER" -> "verify your email for your OwnTag customer account";
            case "CUSTOMER_USERNAME_RECOVERY" -> "verify your email to recover your OwnTag username";
            case "CUSTOMER_PASSWORD_RESET" -> "verify your email to reset your OwnTag password";
            default -> "verify your email";
        };

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Your OwnTag verification code");
        message.setText("Your OwnTag OTP is: " + otp + "\n\nUse this code to " + purposeText
            + ". It expires in 10 minutes. If you did not request this code, you can ignore this email.");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            record.setOtpHash(null);
            record.setExpiresAt(null);
            record.setVerifiedUntil(null);
            repository.save(record);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Could not send the verification email. Check the SMTP configuration and try again.");
        }
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void verifyOtp(String rawEmail, String purpose, String otp) {
        String email = normalizeEmail(rawEmail);
        EmailOtpVerification record = repository.findByEmailAndPurpose(email, purpose)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "OTP is invalid or expired. Request a new code."));
        Instant now = Instant.now();
        if (record.getOtpHash() == null || record.getExpiresAt() == null
                || record.getExpiresAt().isBefore(now) || record.getAttempts() >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "OTP is invalid or expired. Request a new code.");
        }

        if (!passwordEncoder.matches(otp, record.getOtpHash())) {
            record.setAttempts(record.getAttempts() + 1);
            repository.save(record);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect OTP.");
        }

        record.setOtpHash(null);
        record.setExpiresAt(null);
        record.setVerifiedUntil(now.plus(VERIFIED_LIFETIME));
        record.setAttempts(0);
        repository.save(record);
    }

    @Transactional
    public void consumeVerifiedEmail(String rawEmail, String purpose) {
        String email = normalizeEmail(rawEmail);
        EmailOtpVerification record = repository.findByEmailAndPurpose(email, purpose)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Verify your email with an OTP before continuing."));
        if (record.getVerifiedUntil() == null || record.getVerifiedUntil().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email verification is missing or expired. Request and verify a new OTP.");
        }
        record.setVerifiedUntil(null);
        repository.save(record);
    }

    public void sendMessage(String rawEmail, String subject, String body) {
        String email = normalizeEmail(rawEmail);
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email delivery is not configured.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Could not send the email. Check SMTP configuration and try again.");
        }
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address is required.");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > 254 || !normalized.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid email address.");
        }
        return normalized;
    }

}
