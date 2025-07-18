package com.heval.ecommerce.controller;


import com.heval.ecommerce.dto.request.ShippingAddressRequest;
import com.heval.ecommerce.dto.response.ShippingAddressResponse;
import com.heval.ecommerce.entity.ShippingAddress;
import com.heval.ecommerce.mapper.ShippingAddressMapper;
import com.heval.ecommerce.services.JwtService;
import com.heval.ecommerce.services.ShippingAdressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/shipping-address")
@RequiredArgsConstructor
@Validated
public class ShippingAddressController {

    private final ShippingAdressService shippingAdressService;
    private final ShippingAddressMapper shippingAddressMapper;
    private final JwtService jwtService;

    @PostMapping()
    public ResponseEntity<ShippingAddressResponse> saveAddress(
            @RequestHeader("Authorization") String token,
            @RequestBody ShippingAddressRequest request
    ) {
        Long userId = jwtService.extractUserId(token);
        ShippingAddress savedAddress = shippingAdressService.saveAddress(userId, request);
        ShippingAddressResponse response = shippingAddressMapper.toResponse(savedAddress);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<ShippingAddressResponse>> getUserAddresses(
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtService.extractUserId(token);
        List<ShippingAddress> addresses = shippingAdressService.getUserAddresses(userId);
        List<ShippingAddressResponse> responseList = addresses.stream()
                .map(shippingAddressMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responseList);
    }
}
