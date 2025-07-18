package com.heval.ecommerce.repository;

import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProductIdAndCartUserId(Cart cart, Long productId, Long userId);

    List<CartItem> findByCart(Cart cart);
}
