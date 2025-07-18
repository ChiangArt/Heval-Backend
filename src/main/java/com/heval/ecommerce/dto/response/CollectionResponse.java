package com.heval.ecommerce.dto.response;

import java.time.LocalDateTime;

public record CollectionResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        String slug,
        String headlineTitle,
        String descriptionLine1,
        String descriptionLine2,
        boolean active
) {}
