package com.heval.ecommerce.dto.request;


public record ContactInfoRequest(
        String fullName,
        String email,
        String cel,
        String identityDocument
) {}
