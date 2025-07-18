package com.heval.ecommerce.services;

public interface PasswordResetService {

    /**
     * Crea un token de recuperación de contraseña y lo guarda asociado a un correo.
     *
     * @param email correo del usuario
     * @return token generado
     */
    String createToken(String email);

    /**
     * Verifica si el token de recuperación es válido (no expirado ni usado).
     *
     * @param token token a validar
     * @return true si es válido
     */
    boolean isValid(String token);

    /**
     * Obtiene el correo asociado al token.
     *
     * @param token token de recuperación
     * @return email vinculado
     */
    String getEmailByToken(String token);

    /**
     * Marca el token como usado para que no pueda reutilizarse.
     *
     * @param token token a invalidar
     */
    void markAsUsed(String token);
}
