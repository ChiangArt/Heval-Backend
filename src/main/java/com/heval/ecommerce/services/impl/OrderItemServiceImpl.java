package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.entity.OrderItem;
import com.heval.ecommerce.repository.OrderItemRepository;
import com.heval.ecommerce.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByUserId(Long userId) {
            return orderItemRepository.findByUserId(userId);
    }
}
