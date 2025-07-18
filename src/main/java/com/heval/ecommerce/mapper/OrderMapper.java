package com.heval.ecommerce.mapper;

import com.heval.ecommerce.dto.response.OrderResponse;
import com.heval.ecommerce.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ContactInfoMapper contactInfoMapper;
    private final ShippingAddressMapper shippingAddressMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderId(),
                order.getUser().getId(),
                orderItemMapper.toResponse(order.getOrderItems()),
                order.getDeliveryDateTime(),
                shippingAddressMapper.toResponse(order.getShippingAddress()),
                order.getPaymentDetails(),
                order.getTotalPrice(),
                order.getTotalDiscountedPrice(),
                order.getOrderStatus(),
                order.getTotalItems(),
                order.getCreatedAt(),
                contactInfoMapper.toResponse(order.getContactInfo())
        );
    }
}
