package com.heval.ecommerce.dto.response;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String title,
        String description,
        String slug,
        BigDecimal price,
        String descriptionArchetype,
        String material,
        BigDecimal currentPrice,
        Integer discountPercentage,
        LocalDateTime discountUntil,
        List<String> colors,
        Integer quantity,
        List<String> imageUrls,
        LocalDateTime createdAt,
        Long collectionId,
        boolean active
) {
}
