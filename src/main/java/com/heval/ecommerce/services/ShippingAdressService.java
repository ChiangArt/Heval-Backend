package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.request.ShippingAddressRequest;
import com.heval.ecommerce.entity.ShippingAddress;

import java.util.List;

public interface ShippingAdressService {

    ShippingAddress saveAddress(Long userId, ShippingAddressRequest request);
    List<ShippingAddress> getUserAddresses(Long userId);
}
