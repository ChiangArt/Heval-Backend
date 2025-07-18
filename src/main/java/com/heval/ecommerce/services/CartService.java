package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.request.AddCartItemRequest;
import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.entity.CartItem;
import com.heval.ecommerce.entity.User;

public interface CartService {

    public Cart createCart(User user);

    public Cart removeCouponFromCart(Long userId);

    public String addCartItem(Long userId, AddCartItemRequest addCartItem);

    public Cart findUserCart(Long userId);

    public void recalculateTotals(Cart cart);

    public Cart applyCouponToCart(Long userId, String couponCode);

}
