package com.heval.ecommerce.dto.response;

import com.heval.ecommerce.dto.enumeration.DocumentType;

public record ContactInfoResponse(Long id,
                                  String fullName,
                                  String email,
                                  String cel,
                                  String identityDocument,
                                  DocumentType documentType) {
}
