package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.entity.CartItem;
import com.heval.ecommerce.entity.Product;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.CartItemRepository;
import com.heval.ecommerce.repository.CartRepository;
import com.heval.ecommerce.repository.ProductRepository;
import com.heval.ecommerce.services.CartItemService;
import com.heval.ecommerce.services.CartService;
import com.heval.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;



@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ProductService productService;



    @Override
    public CartItem createCartItem(Long cartId, CartItem cartItem) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ApiValidateException("Carrito no encontrado"));

        Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado"));

        if (cartItem.getQuantity() <= 0) {
            throw new ApiValidateException("La cantidad debe ser mayor a cero");
        }

        BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());

        BigDecimal unitPrice = product.getPrice();
        BigDecimal discountedUnitPrice = productService.calculateCurrentPrice(
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getDiscountUntil()
        );

        BigDecimal totalPrice = unitPrice.multiply(quantity);
        BigDecimal totalDiscountedPrice = discountedUnitPrice.multiply(quantity);

        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setPrice(totalPrice);
        cartItem.setDiscountedPrice(totalDiscountedPrice);

        CartItem savedItem = cartItemRepository.save(cartItem);
        cartService.recalculateTotals(cart);

        return savedItem;

    }

    @Override
    public CartItem updateCartItem(Long userId, Long cartItemId, CartItem cartItem) {
        CartItem existingCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ApiValidateException("Item de carrito no encontrado"));

        if (!existingCartItem.getCart().getUser().getId().equals(userId)) {
            throw new ApiValidateException("No autorizado para modificar este carrito");
        }

        int newQuantity = cartItem.getQuantity();
        if (newQuantity <= 0) {
            throw new ApiValidateException("La cantidad debe ser mayor a cero");
        }

        existingCartItem.setQuantity(newQuantity);

        Product product = existingCartItem.getProduct();

        BigDecimal unitPrice = product.getPrice();
        BigDecimal discountedUnitPrice = productService.calculateCurrentPrice(
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getDiscountUntil()
        );
        // ✅ Guardamos solo el precio unitario
        existingCartItem.setPrice(unitPrice);
        existingCartItem.setDiscountedPrice(discountedUnitPrice);

        CartItem updated = cartItemRepository.save(existingCartItem);
        cartService.recalculateTotals(existingCartItem.getCart());

        return updated;
    }


    @Override
    public CartItem isCartItemExist(Cart cart, Long productId, Long userId) {
        return cartItemRepository
                .findByCartAndProductIdAndCartUserId(cart, productId, userId)
                .orElse(null);
    }

    @Override
    public void deleteCartItem(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ApiValidateException("Cart item no encontrado"));

        Cart cart = cartItem.getCart();

        if (!cart.getUser().getId().equals(userId)) {
            throw new ApiValidateException("No autorizado para eliminar este item");
        }

        cartItemRepository.delete(cartItem);
        cartService.recalculateTotals(cart);
        updateCartTotals(cart);

    }

    @Override
    public CartItem findCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ApiValidateException("Cart item no encontrado"));
    }

    private void updateCartTotals(Cart cart) {
        cartService.recalculateTotals(cart);
        cartRepository.save(cart);
    }
}
