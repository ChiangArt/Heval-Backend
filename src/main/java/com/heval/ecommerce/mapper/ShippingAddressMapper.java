package com.heval.ecommerce.mapper;

import com.heval.ecommerce.dto.request.ShippingAddressRequest;
import com.heval.ecommerce.dto.response.ShippingAddressResponse;
import com.heval.ecommerce.entity.ShippingAddress;
import org.springframework.stereotype.Component;

@Component
public class ShippingAddressMapper {

    public ShippingAddress toEntity(ShippingAddressRequest request) {
        return ShippingAddress.builder()
                .fullAddress(request.fullAddress())
                .apartmentOrFloor(request.apartmentOrFloor())
                .district(request.district())
                .province(request.province())
                .department(request.department())
                .reference(request.reference())
                .additionalInfo(request.additionalInfo())
                .build();
    }

    public ShippingAddressResponse toResponse(ShippingAddress address) {
        return new ShippingAddressResponse(
                address.getId(),
                address.getFullAddress(),
                address.getApartmentOrFloor(),
                address.getDistrict(),
                address.getProvince(),
                address.getDepartment(),
                address.getReference(),
                address.getAdditionalInfo()
        );
    }
}
