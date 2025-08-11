package com.heval.ecommerce.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    private boolean active;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}
