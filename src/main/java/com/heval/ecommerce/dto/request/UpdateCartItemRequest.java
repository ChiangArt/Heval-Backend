package com.heval.ecommerce.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(@Min(value = 1, message = "La cantidad debe ser al menos 1")
                                     int quantity) {
}
