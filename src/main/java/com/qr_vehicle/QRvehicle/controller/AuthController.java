package com.qr_vehicle.QRvehicle.controller;

import com.qr_vehicle.QRvehicle.dto.LoginRequest;
import com.qr_vehicle.QRvehicle.dto.LoginResponse;
import com.qr_vehicle.QRvehicle.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(
//     origins = {
//         "https://owntag.in",
//         "https://www.owntag.in"
//     },
//     allowCredentials = "true"
// )
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {

    

    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

    String token = jwtService.generateToken(authentication.getName());

    return new LoginResponse(token, authentication.getName());
}
}