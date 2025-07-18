package com.heval.ecommerce.controller;
import com.heval.ecommerce.dto.response.CartItemResponse;
import com.heval.ecommerce.entity.CartItem;
import com.heval.ecommerce.mapper.CartItemMapper;
import com.heval.ecommerce.services.CartItemService;
import com.heval.ecommerce.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;
    private final JwtService jwtService;


    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String token,
            @RequestBody CartItem cartItem) {

        Long userId = jwtService.extractUserId(token);

        CartItem updatedCartItem = cartItemService.updateCartItem(userId, cartItemId, cartItem);
        CartItemResponse response = cartItemMapper.toResponse(updatedCartItem);

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token);
        cartItemService.deleteCartItem(cartItemId, userId);
        return ResponseEntity.ok("Item eliminado del carrito exitosamente");
    }
}
