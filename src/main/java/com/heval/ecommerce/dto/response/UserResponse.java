package com.heval.ecommerce.dto.response;
import com.heval.ecommerce.dto.enumeration.UserStatus;
import com.heval.ecommerce.dto.enumeration.UserRole;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa los datos públicos de un usuario.
 *
 * @param id                ID del usuario
 * @param name              Nombre completo
 * @param email             Correo electrónico
 * @param identityDocument  Documento de identidad (DNI/RUC)
 * @param cel               Número de celular
 * @param role              Rol del usuario (ADMIN, CLIENT, etc.)
 * @param createdDate       Fecha de creación
 * @param status            Estado actual del usuario (ACTIVE, INACTIVE)
 */


public record UserResponse(Long id,
                           String name,
                           String email,
                           String identityDocument,
                           String cel,
                           UserRole role,
                           LocalDateTime createdDate,
                           UserStatus status) {
}
