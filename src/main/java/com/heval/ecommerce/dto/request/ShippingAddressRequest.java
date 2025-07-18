package com.heval.ecommerce.dto.request;

/**
 * DTO para registrar o actualizar una dirección de envío.
 *
 * @param fullAddress       Dirección completa
 * @param apartmentOrFloor  Departamento o piso
 * @param reference         Referencia adicional (ej. cerca a un parque)
 * @param additionalInfo    Información complementaria
 */
public record ShippingAddressRequest(
        String fullAddress,
        String apartmentOrFloor,
        String district,
        String province,
        String department,
        String reference,
        String additionalInfo
) {}
