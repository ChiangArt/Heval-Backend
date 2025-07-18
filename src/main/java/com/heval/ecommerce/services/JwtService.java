package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.entity.User;

public interface JwtService {
    String generateToken(User user);
    Long extractUserId(String token);
    public UserRole extractUserRole(String token);

    }
