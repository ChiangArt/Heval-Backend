package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.response.OrderItemResponse;
import com.heval.ecommerce.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem saveOrderItem(OrderItem orderItem);

    List<OrderItem> getOrderItemsByUserId(Long userId);
}
