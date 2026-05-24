package com.fjconde.taskmanager.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Respuesta que devuelve la API tras un registro o login exitoso.
 * Contiene el token JWT que el cliente debe usar en las siguientes peticiones.
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
}
