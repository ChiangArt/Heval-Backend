package com.heval.ecommerce.repository;

import com.heval.ecommerce.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByActiveTrue();

}
