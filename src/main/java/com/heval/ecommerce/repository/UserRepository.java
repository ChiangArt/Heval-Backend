package com.heval.ecommerce.repository;

import com.heval.ecommerce.dto.enumeration.UserStatus;
import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    Optional<User> findByCelAndStatus(String cel, UserStatus status);

    Optional<User> findByRoleAndStatus(UserRole role, UserStatus status);


}
