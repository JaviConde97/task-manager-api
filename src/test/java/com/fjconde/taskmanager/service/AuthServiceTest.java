package com.fjconde.taskmanager.service;

import com.fjconde.taskmanager.dto.auth.AuthResponse;
import com.fjconde.taskmanager.dto.auth.LoginRequest;
import com.fjconde.taskmanager.dto.auth.RegistroRequest;
import com.fjconde.taskmanager.entity.Usuario;
import com.fjconde.taskmanager.repository.UsuarioRepository;
import com.fjconde.taskmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del AuthService.
 * Se usan mocks para aislar la lógica del servicio sin tocar la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegistroRequest registroRequest;
    private LoginRequest loginRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        registroRequest = new RegistroRequest();
        registroRequest.setNombre("Javi");
        registroRequest.setEmail("javi@test.com");
        registroRequest.setPassword("12345678");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("javi@test.com");
        loginRequest.setPassword("12345678");

        userDetails = User.withUsername("javi@test.com")
                .password("password-encriptado")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Registro exitoso devuelve un token JWT")
    void registro_exitoso_devuelve_token() {
        // dado que el email no está registrado
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("password-encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(new Usuario());
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generarToken(any(UserDetails.class))).thenReturn("token-jwt");

        AuthResponse respuesta = authService.registro(registroRequest);

        // debe devolver un token
        assertThat(respuesta.getToken()).isEqualTo("token-jwt");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registro con email duplicado lanza excepción")
    void registro_email_duplicado_lanza_excepcion() {
        // dado que el email ya existe
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // debe lanzar una excepción con mensaje claro
        assertThatThrownBy(() -> authService.registro(registroRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con ese email");

        // y no debe guardar nada en la BD
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login exitoso devuelve un token JWT")
    void login_exitoso_devuelve_token() {
        // dado que las credenciales son correctas
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generarToken(any(UserDetails.class))).thenReturn("token-jwt");

        AuthResponse respuesta = authService.login(loginRequest);

        assertThat(respuesta.getToken()).isEqualTo("token-jwt");
    }

    @Test
    @DisplayName("La contraseña se encripta antes de guardar en BD")
    void registro_encripta_la_password() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hash-bcrypt");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generarToken(any())).thenReturn("token");

        authService.registro(registroRequest);

        // debe haber llamado al encoder con la password original
        verify(passwordEncoder).encode("12345678");
        // y guardar un usuario con la password encriptada
        verify(usuarioRepository).save(argThat(u -> u.getPassword().equals("hash-bcrypt")));
    }
}
