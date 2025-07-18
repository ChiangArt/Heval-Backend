package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.enumeration.UserRole;
import com.heval.ecommerce.dto.request.UserRequest;
import com.heval.ecommerce.dto.request.UserUpdateRequest;
import com.heval.ecommerce.dto.response.UserResponse;
import com.heval.ecommerce.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Lista todos los usuarios.
     * @return lista de usuarios como DTOs
     */
    List<User> findAll();

    /**
     * Obtiene un usuario por su ID.
     * @param id ID del usuario
     * @return Usuario encontrado como DTO, si existe
     */
    User getUserById(Long id);

    /**
     * Crea un nuevo usuario.
     * @param user datos del nuevo usuario
     * @return usuario creado como DTO
     */
    User createUser(User user);

    /**
     * Actualiza un usuario existente.
     * @param userId ID del usuario a actualizar
     * @param userUpdateRequest datos actualizados
     * @return usuario actualizado como DTO
     */
    User updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    /**
     * Elimina un usuario por su ID.
     * @param id ID del usuario
     */
    void delete(Long id);

    /**
     * Cambia el estado (activo/inactivo) del usuario.
     * @param id ID del usuario
     * @return usuario actualizado con el nuevo estado como DTO
     */
    User toggleStatus(Long id);

    /**
     * Busca un usuario por su correo electrónico.
     * @param email correo a buscar
     * @return Usuario encontrado como entidad (para uso interno/seguridad), si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuarios por su Rol.
     * @param role rol a buscar
     * @return lista de usuarios como DTOs que tienen el rol especificado
     */
    Optional<User> findByRole(UserRole role);

    /**
     * Verifica si un correo electrónico ya está registrado.
     * @param email correo electrónico a verificar
     * @return true si el correo ya existe, false en caso contrario
     */
    boolean emailExists(String email);

    /**
     * Verifica si un número de celular ya está registrado.
     * @param cel número de celular a verificar
     * @return true si el celular ya existe, false en caso contrario
     */
    boolean celExists(String cel);

    public void updatePasswordByEmail(String email, String newPassword);

}