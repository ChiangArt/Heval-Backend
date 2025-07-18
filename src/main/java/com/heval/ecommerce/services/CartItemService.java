package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.request.*;
import com.heval.ecommerce.dto.response.CartItemResponse;
import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.entity.CartItem;

public interface CartItemService {

    public CartItem createCartItem(Long cartId, CartItem cartItem);

    public CartItem updateCartItem(Long userId,Long id, CartItem cartItem);

    public CartItem isCartItemExist(Cart cart, Long productId, Long userId);

    public void deleteCartItem(Long cartItemId, Long userId);

    public CartItem findCartItemById(Long cartItemId);


}
