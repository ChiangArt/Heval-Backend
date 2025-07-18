package com.heval.ecommerce.repository;

import com.heval.ecommerce.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, Integer> {

    List<ContactInfo> findByUserId(Long userId);

    ContactInfo findByOrderId(Long orderId);
}
