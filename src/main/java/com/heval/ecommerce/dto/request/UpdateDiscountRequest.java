package com.heval.ecommerce.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateDiscountRequest(BigDecimal discountedPrice,
                                    Integer discountPercentage,
                                    LocalDateTime discountUntil) {
}
