package com.heval.ecommerce.repository;

import com.heval.ecommerce.entity.ShippingAddress;
import com.heval.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByUserId(Long userId);

}
