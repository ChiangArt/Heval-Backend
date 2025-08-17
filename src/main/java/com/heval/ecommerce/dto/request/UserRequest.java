package com.heval.ecommerce.dto.request;

import com.heval.ecommerce.dto.enumeration.UserRole;
import jakarta.validation.constraints.*;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        @Size(max = 100, message = "El email no debe exceder los 100 caracteres")
        String email,


        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$",
                message = "La contraseña debe contener al menos un número, una letra mayúscula, una letra minúscula y un carácter especial"
        )
        String password,


        @NotNull(message = "El rol es obligatorio")
        UserRole role,

        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "\\d{6}", message = "El código debe contener 6 dígitos")
         String code) {
}
