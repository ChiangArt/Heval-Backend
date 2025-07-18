package com.heval.ecommerce.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CartItemRequest(@NotNull(message = "El ID del producto es obligatorio")
                              Long productId,

                              @Min(value = 1, message = "La cantidad debe ser al menos 1")
                              int quantity,

                              @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser positivo")
                              BigDecimal price,

                              @DecimalMin(value = "0.0", inclusive = true, message = "El precio con descuento debe ser positivo")
                              BigDecimal discountedPrice) {
}
