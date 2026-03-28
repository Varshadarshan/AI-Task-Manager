package com.varsha.taskmanager.repository;

import com.varsha.taskmanager.model.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpStore, Integer> {

    // ✅ Find latest OTP by email
    Optional<OtpStore> findTopByEmailOrderByExpiryTimeDesc(String email);

    // ✅ Fixed: @Modifying + @Transactional needed for delete query
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpStore o WHERE o.email = :email")
    void deleteByEmail(String email);
}