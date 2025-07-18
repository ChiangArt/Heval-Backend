package com.heval.ecommerce.controller;
import com.heval.ecommerce.dto.request.AddCartItemRequest;
import com.heval.ecommerce.dto.response.CartResponse;
import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.mapper.CartMapper;
import com.heval.ecommerce.services.CartService;
import com.heval.ecommerce.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final JwtService jwtService;


    @PutMapping("/add-item")
    public ResponseEntity<String> addCartItem(
            @RequestHeader("Authorization") String token,
            @RequestBody AddCartItemRequest addCartItem) {
        Long userId = jwtService.extractUserId(token);
        String message = cartService.addCartItem(userId, addCartItem);
        return ResponseEntity.ok(message);
    }

    @GetMapping()
    public ResponseEntity<CartResponse> getUserCart(@RequestHeader("Authorization") String token) {

        Long userId = jwtService.extractUserId(token);
        Cart cart = cartService.findUserCart(userId);
        CartResponse response = cartMapper.toResponse(cart);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/apply-coupon")
    public ResponseEntity<CartResponse> applyCoupon(
            @RequestHeader("Authorization") String token,
            @RequestParam String couponCode
    ) {
        Long userId = jwtService.extractUserId(token);
        Cart updatedCart = cartService.applyCouponToCart(userId, couponCode);
        return ResponseEntity.ok(cartMapper.toResponse(updatedCart));
    }


    @PutMapping("/remove-coupon")
    public ResponseEntity<CartResponse> removeCoupon(
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtService.extractUserId(token);
        Cart updatedCart = cartService.removeCouponFromCart(userId);
        return ResponseEntity.ok(cartMapper.toResponse(updatedCart));
    }


}
