package com.heval.ecommerce.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateProductRequest(String title,
                                   String description,
                                   String material,
                                   BigDecimal price,
                                   String color,
                                   Integer quantity,
                                   Integer discountPercentage,
                                   LocalDateTime discountUntil,
                                   List<String> imageUrls,
                                   Long collectionId) {
}
