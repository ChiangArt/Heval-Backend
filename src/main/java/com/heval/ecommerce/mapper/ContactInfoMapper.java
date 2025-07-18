package com.heval.ecommerce.mapper;

import com.heval.ecommerce.dto.request.ContactInfoRequest;
import com.heval.ecommerce.dto.response.ContactInfoResponse;
import com.heval.ecommerce.entity.ContactInfo;
import com.heval.ecommerce.entity.User;
import com.heval.ecommerce.entity.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContactInfoMapper {

    public ContactInfo toEntity(ContactInfoRequest dto, User user, Order order) {
        return ContactInfo.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .cel(dto.cel())
                .identityDocument(dto.identityDocument())
                .createdAt(LocalDateTime.now())
                .user(user)
                .order(order)
                .build();
    }

    public ContactInfoResponse toResponse(ContactInfo dto) {
        return new ContactInfoResponse(
                dto.getId(),
                dto.getFullName(),
                dto.getEmail(),
                dto.getCel(),
                dto.getIdentityDocument(),
                dto.getDocumentType());
    }
}
