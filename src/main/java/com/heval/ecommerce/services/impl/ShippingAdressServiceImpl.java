package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.dto.request.ShippingAddressRequest;
import com.heval.ecommerce.entity.ShippingAddress;
import com.heval.ecommerce.entity.User;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.ShippingAddressRepository;
import com.heval.ecommerce.repository.UserRepository;
import com.heval.ecommerce.services.ShippingAdressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingAdressServiceImpl implements ShippingAdressService {

    private final UserRepository userRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    @Override
    public ShippingAddress saveAddress(Long userId, ShippingAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiValidateException("Usuario no encontrado"));

        ShippingAddress address = ShippingAddress.builder()
                .fullAddress(request.fullAddress())
                .apartmentOrFloor(request.apartmentOrFloor())
                .reference(request.reference())
                .additionalInfo(request.additionalInfo())
                .user(user)
                .build();

        return shippingAddressRepository.save(address);
    }

    @Override
    public List<ShippingAddress> getUserAddresses(Long userId) {
        return shippingAddressRepository.findByUserId(userId);
    }
}
