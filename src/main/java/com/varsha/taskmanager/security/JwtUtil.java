package com.varsha.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    // ✅ Strong secret key - minimum 32 characters for HS256
    private final String SECRET = "taskmanager-super-secret-key-varsha-2024";

    // ✅ Convert secret string to a secure Key object
    private Key getSigningKey() {
        byte[] keyBytes = SECRET.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // ✅ updated
                .compact();
    }

    // ✅ Returns boolean
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())   // ✅ updated
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Get username from token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())   // ✅ updated
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Get authorities
    public List<GrantedAuthority> getAuthorities(String token) {
        return List.of();
    }

    // Alias
    public String extractUsername(String token) {
        return getUsernameFromToken(token);
    }
}