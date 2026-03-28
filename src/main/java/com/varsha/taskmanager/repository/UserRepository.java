package com.varsha.taskmanager.repository;

import com.varsha.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // ✅ Find user by username (used for login)
    Optional<User> findByUsername(String username);

    // ✅ Find user by email (used for OTP verification)
    Optional<User> findByEmail(String email);
}