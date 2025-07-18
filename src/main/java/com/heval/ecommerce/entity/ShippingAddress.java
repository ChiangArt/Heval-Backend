package com.heval.ecommerce.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shipping_address")
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "apartment_or_floor")
    private String apartmentOrFloor;

    private String district;

    private String province;

    private String department;

    private String reference;

    @Column(name = "additional_info")
    private String additionalInfo;

    @OneToOne(mappedBy = "shippingAddress")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

}
