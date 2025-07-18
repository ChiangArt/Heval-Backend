package com.heval.ecommerce.mapper;
import com.heval.ecommerce.dto.request.CartItemRequest;
import com.heval.ecommerce.dto.response.CartItemResponse;
import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.entity.CartItem;
import com.heval.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {

    public CartItem toEntity(CartItemRequest request, Cart cart, Product product) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.quantity())
                .price(request.price())
                .discountedPrice(request.discountedPrice())
                .build();
    }

    public CartItemResponse toResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getTitle(),
                cartItem.getProduct().getDescription(),
                cartItem.getProduct().getImageUrls(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getDiscountedPrice()
        );
    }
}