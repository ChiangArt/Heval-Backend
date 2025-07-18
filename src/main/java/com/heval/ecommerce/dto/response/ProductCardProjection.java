package com.heval.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProductCardProjection {
    Long getId();
    String getTitle();
    String getSlug();
    BigDecimal getPrice();
    BigDecimal getCurrentPrice();
    Integer getDiscountPercentage();
    LocalDateTime getDiscountUntil();
    String[] getImageUrls();
    String[] getColors();
}
