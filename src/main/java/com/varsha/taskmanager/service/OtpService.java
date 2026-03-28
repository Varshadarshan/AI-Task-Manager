package com.varsha.taskmanager.service;

import com.varsha.taskmanager.model.OtpStore;
import com.varsha.taskmanager.repository.OtpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Generate 6-digit OTP, save to DB and send via email
    @Transactional
    public void generateAndSendOtp(String email) {

        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(email);

        // Generate random 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // Save OTP with 5-minute expiry
        OtpStore otpStore = new OtpStore(email, otp, LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpStore);

        // Send OTP via email
        sendOtpEmail(email, otp);
    }

    // ✅ Verify OTP entered by user
    public boolean verifyOtp(String email, String otpCode) {

        OtpStore otpStore = otpRepository
                .findTopByEmailOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found. Please register again."));

        if (otpStore.isExpired()) {
            otpRepository.deleteByEmail(email);
            throw new RuntimeException("OTP has expired. Please register again.");
        }

        if (!otpStore.getOtpCode().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }

        // ✅ OTP matched — clean up
        otpRepository.deleteByEmail(email);
        return true;
    }

    // ✅ Send email helper
    private void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Task Manager OTP Code");
        message.setText("Hello,\n\nYour OTP code is: " + otp
                + "\n\nThis code is valid for 5 minutes."
                + "\n\nDo not share this code with anyone."
                + "\n\n- Task Manager Team");
        mailSender.send(message);
    }
}