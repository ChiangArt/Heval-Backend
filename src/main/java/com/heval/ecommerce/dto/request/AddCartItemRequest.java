package com.heval.ecommerce.dto.request;

public record AddCartItemRequest(Long productId,
                                 Integer quantity) {
}
