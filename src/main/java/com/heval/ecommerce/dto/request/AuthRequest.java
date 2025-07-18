package com.heval.ecommerce.dto.request;
import jakarta.validation.constraints.*;


public record AuthRequest(@NotBlank(message = "El email es obligatorio")
                          @Email(message = "El email debe ser válido")
                          @Size(max = 100, message = "El email no debe exceder los 100 caracteres")
                          String email,

                          @NotBlank(message = "La contraseña es obligatoria")
                          @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
                          @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                                  message = "La contraseña debe contener al menos un número, una letra mayúscula, una letra minúscula y un carácter especial")
                          String password) {
}
