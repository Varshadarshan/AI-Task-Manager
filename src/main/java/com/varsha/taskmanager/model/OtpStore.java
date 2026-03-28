package com.varsha.taskmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_store")
public class OtpStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ✅ Email to which OTP was sent
    @Column(nullable = false)
    private String email;

    // ✅ The 6-digit OTP code
    @Column(nullable = false)
    private String otpCode;

    // ✅ OTP expires after 5 minutes
    @Column(nullable = false)
    private LocalDateTime expiryTime;

    public OtpStore() {}

    public OtpStore(String email, String otpCode, LocalDateTime expiryTime) {
        this.email = email;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    // ✅ Check if OTP has expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryTime);
    }
}