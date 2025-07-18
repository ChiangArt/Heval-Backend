package com.heval.ecommerce.dto.response;

/**
 * DTO para mostrar la dirección de envío en la respuesta.
 *
 * @param fullAddress       Dirección completa (calle, número, etc.)
 * @param apartmentOrFloor  Departamento o piso, si aplica
 * @param reference         Referencia adicional (ej. cerca a parque)
 * @param additionalInfo    Información complementaria
 */
public record ShippingAddressResponse(
        Long id,
        String fullAddress,
        String apartmentOrFloor,
        String district,
        String province,
        String department,
        String reference,
        String additionalInfo
) {}
