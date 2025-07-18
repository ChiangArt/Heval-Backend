package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.entity.User;
import com.heval.ecommerce.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {


    @Value("${JWT_SECRET}")
    private String secretKey;



    @Override
    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("identityDocument", user.getIdentityDocument());
        claims.put("cel", user.getCel());
        claims.put("role", user.getRole().name());
        claims.put("createdDate", user.getCreatedAt().toString());
        claims.put("status", user.getStatus().name());  

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 día
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Generated token: {}", token);

        return token;
    }

    @Override
    public Long extractUserId(String token) {
        Claims claims = getClaims(cleanToken(token));
        return claims.get("userId", Long.class);
    }

    @Override
    public UserRole extractUserRole(String token) {
        Claims claims = getClaims(cleanToken(token));
        return UserRole.valueOf(claims.get("role", String.class));
    }


    private String cleanToken(String token) {
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    // Metodo privado para extraer los claims (datos) del token
    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
