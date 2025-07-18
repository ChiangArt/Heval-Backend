package com.heval.ecommerce.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record ProductCardResponse(
        Long id,
        String title,
        String slug,
        BigDecimal price,
        Integer discountPercentage,
        BigDecimal currentPrice,
        List<String> imageUrls,
        List<String> colors,
        LocalDateTime discountUntil
) { }

