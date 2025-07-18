package com.heval.ecommerce.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemRequest( Long productId,
                                int quantity) {
}
