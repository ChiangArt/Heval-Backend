package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.dto.enumeration.UserStatus;
import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.dto.request.UserRequest;
import com.heval.ecommerce.dto.request.UserUpdateRequest;
import com.heval.ecommerce.dto.response.UserResponse;
import com.heval.ecommerce.entity.Cart;
import com.heval.ecommerce.entity.User;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.mapper.UserMapper;
import com.heval.ecommerce.repository.CartRepository;
import com.heval.ecommerce.repository.UserRepository;
import com.heval.ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "users")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado con ID: " + id));
    }


    @Override
    public User createUser(User user) {

        User savedUser = userRepository.save(user);


        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        return savedUser;
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado con id: " + userId));

        existingUser.setName(request.name());
        existingUser.setEmail(request.email());
        existingUser.setCel(request.cel());
        existingUser.setIdentityDocument(request.identityDocument());

        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        return userRepository.save(existingUser);
    }


    @Override
    public void delete(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado con id: " + id));

        existingUser.setStatus(UserStatus.INACTIVE);
        userRepository.save(existingUser);
    }

    @Override
    public User toggleStatus(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado con ID: " + id));

        user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
        return userRepository.save(user);

    }

    public void updatePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword)); // Usa BCrypt
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
    }

    @Override
    public Optional<User> findByRole(UserRole role) {
        return userRepository.findByRoleAndStatus(role, UserStatus.ACTIVE);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE).isPresent();
    }

    @Override
    public boolean celExists(String cel) {
        return userRepository.findByCelAndStatus(cel, UserStatus.ACTIVE).isPresent();
    }
}
