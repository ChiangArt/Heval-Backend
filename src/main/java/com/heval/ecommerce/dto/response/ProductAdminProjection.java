package com.heval.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProductAdminProjection {
    Long getId();
    String getTitle();
    String getSlug();
    BigDecimal getPrice();
    BigDecimal getCurrentPrice();
    Integer getDiscountPercentage();
    LocalDateTime getDiscountUntil();
    Integer getQuantity();
    String[] getImageUrls();
    String[] getColors();
}
