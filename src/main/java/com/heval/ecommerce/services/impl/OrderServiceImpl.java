package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.dto.enumeration.OrderStatus;
import com.heval.ecommerce.entity.*;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.CartRepository;
import com.heval.ecommerce.repository.OrderRepository;
import com.heval.ecommerce.repository.ProductRepository;
import com.heval.ecommerce.repository.UserRepository;
import com.heval.ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Order createOrder(ContactInfo contactInfo, ShippingAddress shippingAddress, User user) {

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiValidateException("Carrito con el id del usuario no encontrado"));

        if (cart.getCartItems().isEmpty()) {
            throw new ApiValidateException("El carrito está vacío");
        }

        // Asignar usuario y timestamps a ContactInfo y ShippingAddress
        contactInfo.setUser(existingUser);
        contactInfo.setCreatedAt(LocalDateTime.now());

        shippingAddress.setUser(existingUser);

        // Crear la orden y vincular relaciones
        Order order = Order.builder()
                .user(existingUser)
                .orderId(generateOrderId())
                .shippingAddress(shippingAddress)
                .contactInfo(contactInfo)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        // Relación bidireccional si aplica
        shippingAddress.setOrder(order);
        contactInfo.setOrder(order);

        // Mapear items del carrito a OrderItem
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .discountPrice(cartItem.getDiscountedPrice() != null
                                ? cartItem.getDiscountedPrice()
                                : cartItem.getProduct().getPrice())
                        .userId(existingUser.getId())
                        .deliveryDateTime(LocalDateTime.now().plusDays(3))
                        .order(order)
                        .build())
                .toList();

        order.setOrderItems(orderItems);
        order.setTotalItems(orderItems.stream().mapToInt(OrderItem::getQuantity).sum());
        order.setTotalPrice(calculateTotal(orderItems, false));
        order.setTotalDiscountedPrice(calculateTotal(orderItems, true));

        clearCart(cart);

        return orderRepository.save(order);
    }

    @Override
    public String retryPayment(String orderId) {
        Order order = findOrderByOrderId(orderId);

        if (order.getOrderStatus() == OrderStatus.PAID ||
                order.getOrderStatus() == OrderStatus.CANCELLED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new ApiValidateException("No se puede reintentar el pago para esta orden");
        }

        String paymentUrl = "https://tu-pasarela.com/pagar?external_reference=" + order.getOrderId();

        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);

        return paymentUrl;
    }

    @Override
    public Order findOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ApiValidateException("Orden no encontrada"));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order placedOrder(String orderId) {
        Order order = findOrderByOrderId(orderId);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        return orderRepository.save(order);
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void updateStatus(String orderId, OrderStatus status) {
        Order order = findOrderByOrderId(orderId);
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Order confirmOrder(String orderId) {
        Order order = findOrderByOrderId(orderId);

        if (!order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new ApiValidateException("La orden no está en estado pendiente de pago");
        }

        order.setOrderStatus(OrderStatus.PAID);
        reduceStockAfterPayment(order);

        return orderRepository.save(order);
    }

    @Override
    public void reduceStockAfterPayment(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int nuevaCantidad = product.getQuantity() - item.getQuantity();
            product.setQuantity(Math.max(0, nuevaCantidad));
            productRepository.save(product);
        }
    }

    @Override
    public Order cancelOrder(String orderId) {
        Order order = findOrderByOrderId(orderId);
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public Order shippedOrder(String orderId) {
        Order order = findOrderByOrderId(orderId);
        order.setOrderStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    @Override
    public Order deliveredOrder(String orderId) {
        Order order = findOrderByOrderId(orderId);
        order.setOrderStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ApiValidateException("La orden no existe");
        }
        orderRepository.deleteById(orderId);
    }

    private String generateOrderId() {
        String prefix = "ORD-";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + date + "-" + random;
    }

    private BigDecimal calculateTotal(List<OrderItem> items, boolean isDiscounted) {
        return items.stream()
                .map(item -> {
                    BigDecimal price = isDiscounted
                            ? Objects.requireNonNullElse(item.getDiscountPrice(), BigDecimal.ZERO)
                            : item.getPrice();
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cart.setTotalItem(0);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalDiscountPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
