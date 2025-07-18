package com.heval.ecommerce.dto.request;
import com.heval.ecommerce.dto.enumeration.UserRole;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.*;

public record UserUpdateRequest(@NotBlank(message = "El nombre es obligatorio")
                                @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
                                String name,

                                @NotBlank(message = "El email es obligatorio")
                                @Email(message = "El email debe ser válido")
                                @Size(max = 100, message = "El email no debe exceder los 100 caracteres")
                                String email,

//                                @NotBlank(message = "Identity document is required")
                                @Nullable
                                @Pattern(regexp = "^(\\d{8}|\\d{11})$", message = "Identity document must have 8 or 11 digits")
                                String identityDocument,

//                                @NotBlank(message = "El celular es obligatorio")
                                @Nullable
                                @Pattern(regexp = "^\\+51[9][0-9]{8}$", message = "El número de celular debe ser válido")
                                String cel,

//                                @NotBlank(message = "La contraseña es obligatoria")
                                @Nullable
                                @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
                                @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "La contraseña debe contener al menos un número, una letra mayúscula, una letra minúscula y un carácter especial")
                                String password,

                                @NotNull(message = "El rol es obligatorio")
                                UserRole role) {
}
