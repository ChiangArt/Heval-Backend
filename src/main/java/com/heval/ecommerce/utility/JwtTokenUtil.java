package com.heval.ecommerce.utility;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;

    public JwtTokenUtil(@Value("${app.jwt.secret}") String secretKeyString) {
        this.secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String generateAnonymousToken() {
        long now = System.currentTimeMillis();
        long expiry = now + 1000 * 60 * 60 * 24; // 1 día válido

        return Jwts.builder()
                .setSubject("anonymous")
                .claim("roles", "ROLE_ANONYMOUS")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
