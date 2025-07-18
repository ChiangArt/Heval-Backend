package com.heval.ecommerce.dto.response;

import com.heval.ecommerce.dto.enumeration.OrderStatus;
import com.heval.ecommerce.entity.PaymentDetails;
import com.heval.ecommerce.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id,
                            String orderId,
                            Long userId,
                            List<OrderItemResponse> orderItems,
                            LocalDateTime deliveryDateTime,
                            ShippingAddressResponse shippingAddress,
                            PaymentDetails paymentDetails,
                            BigDecimal totalPrice,
                            BigDecimal totalDiscountedPrice,
                            OrderStatus orderStatus,
                            int totalItems,
                            LocalDateTime createdAt,
                            ContactInfoResponse contactInfo) {
}
