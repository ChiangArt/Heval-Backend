package com.heval.ecommerce.dto.response;

import com.heval.ecommerce.dto.enumeration.UserRole;

public record AuthResponse(Long userId,
                           String name,
                           String email,
                           String token,
                           UserRole role) {
}
