package com.heval.ecommerce.dto.response;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public record CartItemResponse(Long id,
                               Long productId,
                               String productTitle,
                               String productDescription,
                               List<String> imageUrl,
                               int quantity,
                               BigDecimal price,
                               BigDecimal discountedPrice
                               ) {
}
