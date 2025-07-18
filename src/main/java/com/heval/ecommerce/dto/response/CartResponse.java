package com.heval.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        BigDecimal totalPrice,
        int totalItem,
        BigDecimal totalDiscountPrice,
        BigDecimal discount,
        CouponResponse coupon,
        List<CartItemResponse> cartItems
) {
}

