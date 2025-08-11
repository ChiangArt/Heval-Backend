package com.heval.ecommerce.mapper;
import com.heval.ecommerce.dto.enumeration.UserStatus;
import com.heval.ecommerce.dto.request.UserRequest;
import com.heval.ecommerce.dto.request.UserUpdateRequest;
import com.heval.ecommerce.dto.response.UserResponse;
import com.heval.ecommerce.entity.User;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    /* CREAR NUEVO USER DESDE EL UserRequest */
    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /* ACTUALIZAR ENTIDAD EXISTENTE CON UserpdateRequest */
    public void updateEntity(User existingUser, UserUpdateRequest request) {
        if (StringUtils.hasText(request.name())) {
            existingUser.setName(request.name());
        }
        if (StringUtils.hasText(request.email())) {
            existingUser.setEmail(request.email());
        }
        if (StringUtils.hasText(request.identityDocument())) {
            existingUser.setIdentityDocument(request.identityDocument());
        }
        if (StringUtils.hasText(request.cel())) {
            existingUser.setCel(request.cel());
        }
        if (request.role() != null) {
            existingUser.setRole(request.role());
        }
        if (StringUtils.hasText(request.password())) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }
    }

    /* CONVIERTE LA ENTIDAD A RESPUESTA */
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIdentityDocument(),
                user.getCel(),
                user.getRole(),
                user.getCreatedAt(),
                user.getStatus()
        );
    }


}
