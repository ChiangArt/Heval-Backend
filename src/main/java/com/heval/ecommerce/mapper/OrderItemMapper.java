package com.heval.ecommerce.mapper;

import com.heval.ecommerce.dto.request.OrderItemRequest;
import com.heval.ecommerce.dto.response.OrderItemResponse;
import com.heval.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemMapper {

    public OrderItem toEntity(OrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setQuantity(request.quantity());

        return item;
    }

    public OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getTitle(),
                item.getProduct().getDescription(),
                item.getProduct().getImageUrls(), // ← ya es List<String>
                item.getQuantity(),
                item.getPrice(),
                item.getDiscountPrice()
        );
    }


    public List<OrderItemResponse> toResponse(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toResponse)
                .toList();
    }

}
