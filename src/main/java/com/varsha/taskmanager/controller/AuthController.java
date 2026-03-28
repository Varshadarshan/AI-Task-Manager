package com.varsha.taskmanager.controller;

import com.varsha.taskmanager.dto.AuthRequest;
import com.varsha.taskmanager.dto.AuthResponse;
import com.varsha.taskmanager.dto.RegisterRequest;
import com.varsha.taskmanager.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ✅ STEP 1: Register new user — sends OTP to email
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // ✅ STEP 2: Verify OTP — activates account
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        return authService.verifyOtp(email, otp);
    }

    // ✅ STEP 3: Login — only verified users allowed
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    // ✅ NEW: Resend OTP — for unverified users whose OTP expired
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email) {
        return authService.resendOtp(email);
    }

    // ✅ NEW: Forgot Password — sends OTP to registered email
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    // ✅ NEW: Reset Password — verify OTP + set new password
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        return authService.resetPassword(email, otp, newPassword);
    }
}