package com.ecosphere.controller;

import com.ecosphere.dto.SendOtpRequest;
import com.ecosphere.dto.VerifyOtpRequest;
import com.ecosphere.service.AuthService;
import com.ecosphere.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.ecosphere.service.AuthService;
import com.ecosphere.dto.LoginRequest;
import com.ecosphere.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final AuthService authService;

    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody SendOtpRequest request) {

        otpService.sendOtp(request.getEmail());
        return "OTP sent successfully";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody VerifyOtpRequest request) {

        otpService.verifyOtpAndRegister(request);
        return "User registered successfully";
    }
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestBody SendOtpRequest request) {

        otpService.sendOtp(request.getEmail());
        return "OTP resent successfully";
    }
}