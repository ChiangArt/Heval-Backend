package com.heval.ecommerce.controller;

import com.heval.ecommerce.dto.enumeration.OrderStatus;
import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.dto.request.OrderRequest;
import com.heval.ecommerce.dto.response.OrderResponse;
import com.heval.ecommerce.entity.ContactInfo;
import com.heval.ecommerce.entity.Order;
import com.heval.ecommerce.entity.ShippingAddress;
import com.heval.ecommerce.entity.User;
import com.heval.ecommerce.mapper.OrderMapper;
import com.heval.ecommerce.services.JwtService;
import com.heval.ecommerce.services.OrderService;
import com.heval.ecommerce.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final JwtService jwtService;

    @PostMapping()
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid OrderRequest orderRequest) {

        Long userId = jwtService.extractUserId(token);
        User user = userService.getUserById(userId);

        // Construir ContactInfo
        ContactInfo contactInfo = ContactInfo.builder()
                .fullName(orderRequest.fullName())
                .email(orderRequest.email())
                .cel(orderRequest.cel())
                .identityDocument(orderRequest.identityDocument())
                .documentType(orderRequest.documentType())
                .build();

        // Construir ShippingAddress
        ShippingAddress shippingAddress = ShippingAddress.builder()
                .fullAddress(orderRequest.fullAddress())
                .apartmentOrFloor(orderRequest.apartmentOrFloor())
                .district(orderRequest.district())
                .province(orderRequest.province())
                .department(orderRequest.department())
                .reference(orderRequest.reference())
                .additionalInfo(orderRequest.additionalInfo())
                .build();

        // Crear orden sin boleta PDF
        Order createdOrder = orderService.createOrder(contactInfo, shippingAddress, user);

        OrderResponse response = orderMapper.toResponse(createdOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token);
        List<Order> userOrders = orderService.getOrdersByUserId(userId);
        List<OrderResponse> responseList = userOrders.stream()
                .map(orderMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@RequestHeader("Authorization") String token,
                                               @PathVariable String orderId,
                                               @RequestParam OrderStatus status) {
        // Aquí puedes validar roles si quieres
        orderService.updateStatus(orderId, status);
        return ResponseEntity.ok("Estado actualizado a: " + status);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderByOrderId(@RequestHeader("Authorization") String token,
                                                           @PathVariable String orderId) {
        Long userId = jwtService.extractUserId(token);
        Order order = orderService.findOrderByOrderId(orderId);

        // Validación: el usuario debe ser el dueño de la orden
        if (!order.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body(null);
        }

        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/retry-payment/{orderId}")
    public ResponseEntity<String> retryPayment(@RequestHeader("Authorization") String token,
                                               @PathVariable String orderId) {
        Long userId = jwtService.extractUserId(token);
        Order order = orderService.findOrderByOrderId(orderId);

        if (!order.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        String paymentUrl = orderService.retryPayment(orderId);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestHeader("Authorization") String token) {
        UserRole role = jwtService.extractUserRole(token);

        if (role != UserRole.ADMIN) {
            return ResponseEntity.status(403).body(null);
        }

        List<Order> orders = orderService.getAllOrders();
        List<OrderResponse> responseList = orders.stream()
                .map(orderMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responseList);
    }
}
