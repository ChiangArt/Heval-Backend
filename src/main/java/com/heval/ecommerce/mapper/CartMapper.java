package com.heval.ecommerce.mapper;


import com.heval.ecommerce.dto.response.CartItemResponse;
import com.heval.ecommerce.dto.response.CartResponse;
import com.heval.ecommerce.dto.response.CouponResponse;
import com.heval.ecommerce.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    public CartResponse toResponse(Cart entity) {
        List<CartItemResponse> itemResponses = entity.getCartItems()
                .stream()
                .map(cartItemMapper::toResponse)
                .toList();

        CouponResponse couponResponse = null;

        if (entity.getCoupon() != null) {
            couponResponse = new CouponResponse(
                    entity.getCoupon().getCode(),
                    entity.getCoupon().getDiscountPercentage()
            );
        }

        return new CartResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTotalPrice(),
                entity.getTotalItem(),
                entity.getTotalDiscountPrice(),
                entity.getDiscount(),
                couponResponse,
                itemResponses
        );
    }

}
