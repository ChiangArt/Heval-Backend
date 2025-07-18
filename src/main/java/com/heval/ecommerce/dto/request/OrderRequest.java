package com.heval.ecommerce.dto.request;
import com.heval.ecommerce.dto.enumeration.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
                           @NotBlank String fullName,
                           @Email @NotBlank String email,
                           @NotBlank String cel,
                           @NotBlank String identityDocument,
                           @NotNull DocumentType documentType,

                           // Shipping Address
                           @NotBlank String fullAddress,
                           String apartmentOrFloor,
                           @NotBlank String district,
                           @NotBlank String province,
                           @NotBlank String department,
                           String reference,
                           String additionalInfo) {
}
