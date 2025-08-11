package com.heval.ecommerce.entity;


import com.heval.ecommerce.dto.enumeration.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "order_id")
    private String orderId;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_discount")
    private BigDecimal couponDiscount;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime deliveryDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private ShippingAddress shippingAddress;

    @Embedded
    private PaymentDetails paymentDetails;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_info_id")
    private ContactInfo contactInfo;

    private BigDecimal totalPrice;

    private BigDecimal totalDiscountedPrice;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private int totalItems;

    private LocalDateTime createdAt;
}
