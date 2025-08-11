package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.enumeration.OrderStatus;
import com.heval.ecommerce.entity.ContactInfo;
import com.heval.ecommerce.entity.Order;
import com.heval.ecommerce.entity.ShippingAddress;
import com.heval.ecommerce.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order createOrder(ContactInfo contactInfo, ShippingAddress address, User user);
    public List<Order> getOrdersByDate(String date);
    public List<Order> getOrdersByDateRange(String start, String end);

    Order findOrderByOrderId(String orderId);

    void saveOrder(Order order);

    Order placedOrder(String orderId);

    Order confirmOrder(String orderId);

    Order cancelOrder(String orderId);


    Order shippedOrder(String orderId);

    Order deliveredOrder(String orderId);

    List<Order> getOrdersByUserId(Long userId);

    String retryPayment(String orderId);

    void reduceStockAfterPayment(Order order);


    void deleteOrder(Long orderId);

    List<Order> getAllOrders();

    void updateStatus(String orderId, OrderStatus status);


}
