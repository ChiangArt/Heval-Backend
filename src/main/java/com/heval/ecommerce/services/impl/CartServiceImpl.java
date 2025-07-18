package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.dto.request.AddCartItemRequest;
import com.heval.ecommerce.dto.response.CartItemResponse;
import com.heval.ecommerce.dto.response.CartResponse;
import com.heval.ecommerce.dto.response.ProductResponse;
import com.heval.ecommerce.entity.*;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.mapper.CartMapper;
import com.heval.ecommerce.repository.*;
import com.heval.ecommerce.services.CartItemService;
import com.heval.ecommerce.services.CartService;
import com.heval.ecommerce.services.ProductService;
import com.heval.ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {


    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CouponRepository couponRepository;



    @Override
    public Cart createCart(User user) {
        Cart newCart = Cart.builder()
                .user(user)
                .totalItem(0)
                .totalPrice(BigDecimal.ZERO)
                .build();

        return cartRepository.save(newCart);
    }

    @Override
    public String addCartItem(Long userId, AddCartItemRequest addCartItem) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiValidateException("Carrito no encontrado para el usuario"));

        Product product = productRepository.findById(addCartItem.productId())
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado"));

        if (product.getQuantity() < addCartItem.quantity()) {
            throw new ApiValidateException("No hay suficiente stock disponible para el producto");
        }

        Optional<CartItem> optionalCartItem = cartItemRepository
                .findByCartAndProductIdAndCartUserId(cart, addCartItem.productId(), userId);

        BigDecimal unitPrice = product.getPrice();

        BigDecimal discountedUnitPrice = productService.calculateCurrentPrice(
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getDiscountUntil()
        );

        if (optionalCartItem.isPresent()) {
            CartItem existingItem = optionalCartItem.get();
            existingItem.setQuantity(addCartItem.quantity());
            existingItem.setPrice(unitPrice);
            existingItem.setDiscountedPrice(discountedUnitPrice);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(addCartItem.quantity())
                    .price(unitPrice)
                    .discountedPrice(discountedUnitPrice)
                    .build();
            cartItemRepository.save(newItem);
        }

        recalculateTotals(cart);
        return "Item agregado al carrito exitosamente";
    }

    @Override
    public Cart findUserCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiValidateException("Carrito no encontrado para el usuario"));
    }

    @Override
    public Cart applyCouponToCart(Long userId, String couponCode) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiValidateException("Carrito no encontrado para el usuario"));

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new ApiValidateException("Cupón no válido"));

        if (!coupon.isActive() || coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiValidateException("El cupón está inactivo o ha expirado");
        }

        // Calcular el descuento solo sobre el total con descuento de productos
        BigDecimal discount = cart.getTotalDiscountPrice()
                .multiply(BigDecimal.valueOf(coupon.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100));

        cart.setCoupon(coupon);
        cart.setDiscount(discount.setScale(2));
        cart.setTotalDiscountPrice(cart.getTotalDiscountPrice().subtract(discount).setScale(2));

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeCouponFromCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        cart.setCoupon(null);
        cart.setDiscount(BigDecimal.ZERO);

        recalculateTotals(cart);

        return cart;
    }




    @Override
    public void recalculateTotals(Cart cart) {

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscountedPrice = cartItems.stream()
                .map(item -> item.getDiscountedPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalItem(totalItems);
        cart.setTotalPrice(totalPrice);
        cart.setTotalDiscountPrice(totalDiscountedPrice);

        cartRepository.save(cart);
    }
}
