package com.varsha.taskmanager.service;

import com.varsha.taskmanager.dto.AuthRequest;
import com.varsha.taskmanager.dto.AuthResponse;
import com.varsha.taskmanager.dto.RegisterRequest;
import com.varsha.taskmanager.model.User;
import com.varsha.taskmanager.repository.UserRepository;
import com.varsha.taskmanager.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    // ✅ STEP 1: Register — save user as unverified + send OTP
    public String register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setVerified(false);

        userRepository.save(user);
        otpService.generateAndSendOtp(request.getEmail());

        return "Registration successful! OTP sent to " + request.getEmail() + ". Please verify to activate your account.";
    }

    // ✅ STEP 2: Verify OTP — mark user as verified
    public String verifyOtp(String email, String otpCode) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        if (user.isVerified()) {
            return "Account already verified. Please login.";
        }

        otpService.verifyOtp(email, otpCode);

        user.setVerified(true);
        userRepository.save(user);

        return "Email verified successfully! You can now login.";
    }

    // ✅ STEP 3: Login — only allow verified users
    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified. Please check your email for the OTP.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    // ✅ NEW: Resend OTP — only for unverified users
    public String resendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (user.isVerified()) {
            return "Account already verified. Please login.";
        }

        // Delete old OTP and send fresh one
        otpService.generateAndSendOtp(email);

        return "New OTP sent to " + email + ". Valid for 5 minutes.";
    }

    // ✅ NEW: Forgot Password — send OTP to registered email
    public String forgotPassword(String email) {

        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        otpService.generateAndSendOtp(email);

        return "Password reset OTP sent to " + email + ". Valid for 5 minutes.";
    }

    // ✅ NEW: Reset Password — verify OTP then update password
    public String resetPassword(String email, String otpCode, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Verify OTP
        otpService.verifyOtp(email, otpCode);

        // Update with new hashed password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password reset successfully! You can now login with your new password.";
    }
}