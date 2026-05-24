package com.fjconde.taskmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios del JwtService.
 * Se inyectan los valores de secreto y expiración directamente
 * sin levantar el contexto de Spring.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Inyectamos los valores del application.properties manualmente
        ReflectionTestUtils.setField(jwtService, "secreto",
                "clave-secreta-muy-larga-que-debe-tener-al-menos-256-bits-para-HS256");
        ReflectionTestUtils.setField(jwtService, "expiracion", 86400000L);

        userDetails = User.withUsername("javi@test.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Generar token devuelve un valor no nulo")
    void generar_token_devuelve_valor_no_nulo() {
        String token = jwtService.generarToken(userDetails);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("Extraer email del token devuelve el email correcto")
    void extraer_email_devuelve_el_correcto() {
        String token = jwtService.generarToken(userDetails);

        String email = jwtService.extraerEmail(token);

        assertThat(email).isEqualTo("javi@test.com");
    }

    @Test
    @DisplayName("Token recién generado es válido para el usuario")
    void token_recien_generado_es_valido() {
        String token = jwtService.generarToken(userDetails);

        boolean esValido = jwtService.esTokenValido(token, userDetails);

        assertThat(esValido).isTrue();
    }

    @Test
    @DisplayName("Token de otro usuario no es válido para este usuario")
    void token_de_otro_usuario_no_es_valido() {
        UserDetails otroUsuario = User.withUsername("otro@test.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String tokenDeOtro = jwtService.generarToken(otroUsuario);

        // el token de otro@test.com no debe ser válido para javi@test.com
        boolean esValido = jwtService.esTokenValido(tokenDeOtro, userDetails);

        assertThat(esValido).isFalse();
    }
}
